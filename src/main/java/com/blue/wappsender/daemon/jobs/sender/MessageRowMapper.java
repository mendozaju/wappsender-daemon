package com.blue.wappsender.daemon.jobs.sender;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.tree.TreePath;

import org.springframework.jdbc.core.RowMapper;

import com.blue.wappsender.daemon.jobs.sender.reder.WhatSappMessageDTO;

public class MessageRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet arg0, int arg1) throws SQLException {
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new WhatSappMessageDTO();
		
	}


}
