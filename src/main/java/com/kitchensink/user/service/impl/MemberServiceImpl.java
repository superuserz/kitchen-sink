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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kitchensink.user.entity.Member;
import com.kitchensink.user.enums.UserRole;
import com.kitchensink.user.repository.MemberRepository;
import com.kitchensink.user.service.MemberService;
import com.kitchensink.user.utils.WorkbookUtils;

@Service
public class MemberServiceImpl implements MemberService {

	@Autowired
	private MemberRepository memberRepository;

	public MemberServiceImpl(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	// Define the formatter
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss");

	@Override
	public Member lookupMemberById(String id) {
		Optional<Member> member = memberRepository.findById(id);
		if (member.isPresent()) {
			return member.get();
		} else {
			return null;
		}
	}

	@Override
	public List<Member> listAllMembers() {
		return memberRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
	}

	@Override
	public Member getProfile() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = (String) authentication.getPrincipal();
		return memberRepository.findByEmail(email).get();
	}

	@Override
	public Workbook exportMembersReport() {

		List<Member> members = memberRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

		Workbook reportWorkbook = new XSSFWorkbook();
		int rowNumber = 0;
		Sheet sheet = reportWorkbook.createSheet("Members");
		populateMemberDetails(reportWorkbook, rowNumber, sheet, members);
		return reportWorkbook;
	}

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
}
