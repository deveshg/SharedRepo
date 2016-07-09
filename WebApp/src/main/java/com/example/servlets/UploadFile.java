package com.example.servlets;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;

@WebServlet("/UploadFile")
public class UploadFile extends HttpServlet {
	final static Logger logger = Logger.getLogger(UploadFile.class);
	private static final long serialVersionUID = 1L;

	public UploadFile() {
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		logger.debug(req);
		final boolean isMultiPart = ServletFileUpload.isMultipartContent(req);
		if (!isMultiPart) {
			sendResponse(resp, "Not a multi-part form");
			return;
		}
		final List<FileItem> items = getFileItems(req);
		if (items == null) {
			sendResponse(resp, "file items are null");
			return;
		}
		if(items.size()==0){
			sendResponse(resp, "file items are empty");
			return;
		}
		logger.debug("Upload Items: " + items.toString());
		final File path = getRootPath();
		final StringBuilder sb = new StringBuilder();
		for (final FileItem item : items) {
			if (!item.isFormField()) {
				storeFileItem(item,path,sb);
			} else {
				/* TODO: handle simple form data */
				logger.debug("sample form data" + item);
			}
		}
		String message = sb.toString();
		sendResponse(resp, message);
	}

	private void sendResponse(HttpServletResponse resp, String message) throws IOException {
		resp.setContentType(ContentType.DEFAULT_TEXT.getMimeType());
		resp.getWriter().println(message);
		resp.getWriter().flush();
	}

	private List<FileItem> getFileItems(HttpServletRequest req) {
		/* Create a factory for disk-based file items */
		final FileItemFactory factory = new DiskFileItemFactory();
		/* Create a new file upload handler */
		final ServletFileUpload upload = new ServletFileUpload(factory);
		List<FileItem> items = null;
		try {
			items = upload.parseRequest(req);
		} catch (FileUploadException e) {
			logger.error(e);
		}
		return items;
	}

	private void storeFileItem(FileItem item, File path, StringBuilder sb) {
		logger.debug("processing: " + item);
		final String fileName = item.getName();
		final File uploadedFile = new File(path + File.separator + fileName);
		logger.debug("upload path: " + uploadedFile.getAbsolutePath());
		try {
			item.write(uploadedFile);
			logger.debug("uploaded..!");
			sb.append(uploadedFile.toString());
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private File getRootPath() {
		final String root = getServletContext().getRealPath("/");
		final File path = new File(root + File.separator + "uploads");
		if (!path.exists()) {
			logger.debug("creating directory :" + path);
			path.mkdirs();
		}
		return path;
	}
}
