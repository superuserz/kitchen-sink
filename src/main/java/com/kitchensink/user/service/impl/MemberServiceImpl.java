package com.kitchensink.user.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kitchensink.user.entity.Member;
import com.kitchensink.user.enums.UserRole;
import com.kitchensink.user.exception.AuthenticationException;
import com.kitchensink.user.repository.MemberRepository;
import com.kitchensink.user.requests.MemberCriteriaRequest;
import com.kitchensink.user.service.MemberService;
import com.kitchensink.user.utils.WorkbookUtils;

/**
 * The Class MemberServiceImpl.
 */
@Service
public class MemberServiceImpl implements MemberService {

	/** The member repository. */
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	private static final Logger LOGGER = LoggerFactory.getLogger(MemberServiceImpl.class);

	/**
	 * Instantiates a new member service impl.
	 *
	 * @param memberRepository the member repository
	 */
	public MemberServiceImpl(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	/** The formatter. */
	// Define the formatter
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss");

	/**
	 * Lookup member by id.
	 *
	 * @param id the id
	 * @return the member
	 */
	@Override
	public Member lookupMemberById(String id) {
		Optional<Member> member = memberRepository.findById(id);
		if (member.isPresent()) {
			return member.get();
		} else {
			return null;
		}
	}

	/**
	 * List all members.
	 *
	 * @return the list
	 */
	@Override
	public List<Member> listAllMembers() {
		return memberRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
	}

	/**
	 * Gets the profile.
	 *
	 * @return the profile
	 */
	@Override
	public Member getProfile() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getPrincipal() == null) {
			throw new AuthenticationException("User not authenticated");
		}

		String email = (String) authentication.getPrincipal();
		return memberRepository.findByEmail(email)
				.orElseThrow(() -> new AuthenticationException("Member not found with email: " + email));
	}

	/**
	 * Export members report.
	 *
	 * @return the workbook
	 */
	@Override
	public Workbook exportMembersReport() {

		List<Member> members = memberRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

		Workbook reportWorkbook = new XSSFWorkbook();
		int rowNumber = 0;
		Sheet sheet = reportWorkbook.createSheet("Members");
		populateMemberDetails(reportWorkbook, rowNumber, sheet, members);
		return reportWorkbook;
	}

	/**
	 * Populate member details.
	 *
	 * @param reportWorkbook the report workbook
	 * @param rowNumber      the row number
	 * @param sheet          the sheet
	 * @param members        the members
	 */
	private void populateMemberDetails(Workbook reportWorkbook, int rowNumber, Sheet sheet, List<Member> members) {
		CellStyle centerAlignedStyle = reportWorkbook.createCellStyle();
		centerAlignedStyle.setAlignment(HorizontalAlignment.CENTER);
		centerAlignedStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		Row headerRow = sheet.createRow(rowNumber++);

		String[] headers = new String[] { "Name", "Email", "Phone Number", "Registration Date", "Roles" };

		// Populate Excel Headers
		WorkbookUtils.setRow(headerRow, reportWorkbook, 0, headers, true, (short) -1,
				IndexedColors.DARK_BLUE.getIndex());

		for (Member dto : members) {
			Row dataRow = sheet.createRow(rowNumber++);

			int colIdx = 0;

			// Name
			Cell nameCell = dataRow.createCell(colIdx++);
			nameCell.setCellValue(dto.getName());
			nameCell.setCellStyle(centerAlignedStyle);

			// Email
			Cell emailCell = dataRow.createCell(colIdx++);
			emailCell.setCellValue(dto.getEmail());
			emailCell.setCellStyle(centerAlignedStyle);

			// Phone Number
			Cell phoneCell = dataRow.createCell(colIdx++);
			phoneCell.setCellValue(dto.getPhoneNumber());
			phoneCell.setCellStyle(centerAlignedStyle);

			// Registration Date
			Cell dateCell = dataRow.createCell(colIdx++);
			dateCell.setCellValue(dto.getRegisteredOn().format(formatter));
			dateCell.setCellStyle(centerAlignedStyle);

			// Registration Date
			Cell roleCell = dataRow.createCell(colIdx++);
			roleCell.setCellValue(dto.getRoles().stream().map(UserRole::name).collect(Collectors.joining(",")));
			roleCell.setCellStyle(centerAlignedStyle);

		}

		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

	}

	/**
	 * Delete member by id.
	 *
	 * @param id the id
	 * @return true, if successful
	 */
	@Override
	public boolean deleteMemberById(String id) {
		Optional<Member> member = memberRepository.findById(id);
		if (member.isPresent()) {
			memberRepository.deleteById(id);
			return true;
		}
		return false;
	}

	@Override
	public Page<Member> searchMembers(MemberCriteriaRequest criteria) {

		LOGGER.info("Criteria Request Received - {}", criteria.toString());

		Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(),
				criteria.getDirection().equalsIgnoreCase("asc") ? Sort.by(criteria.getSortBy()).ascending()
						: Sort.by(criteria.getSortBy()).descending());

		Query query = new Query().with(pageable);

		if (criteria.getName() != null && !criteria.getName().isEmpty()) {
			query.addCriteria(Criteria.where("name").regex(criteria.getName(), "i")); // case-insensitive
		}

		if (criteria.getEmail() != null && !criteria.getEmail().isEmpty()) {
			query.addCriteria(Criteria.where("email").regex(criteria.getEmail(), "i"));
		}

		if (criteria.getRole() != null && !criteria.getRole().isEmpty()) {
			query.addCriteria(Criteria.where("roles").in(criteria.getRole()));
		}

		List<Member> members = mongoTemplate.find(query, Member.class);
		long count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Member.class);

		return new PageImpl<>(members, pageable, count);
	}
}
