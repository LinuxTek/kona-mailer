/*
 * Copyright (C) 2011 LinuxTek, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.mailer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import com.linuxtek.kona.util.KByteArrayDataSource;
import com.linuxtek.kona.util.KStringUtil;

public class KMailer {
    private static Logger logger = Logger.getLogger(KMailer.class);

	private String mailhost = "localhost";
	private String username = null;
	private String password = null;
	private String from = null;
    private String sender = null;
    private String returnPath = null;
	private String[] to = null;
    private String[] replyTo = null;
	private String[] cc = null;
	private String[] bcc = null;
	private String subject = null;
	private String textBody = null;
	private String htmlBody = null;
	private Date sentDate = null;
	private boolean isRichText = false;
	private boolean isHTML = false;

	private List<File> files = null;
	private List<URL> urls = null;
	private List<KByteArrayDataSource> byteArrays = null;

	private class MyAuthenticator extends javax.mail.Authenticator {
		private String username = null;
		private String password = null;
		private javax.mail.PasswordAuthentication pw = null;

		public MyAuthenticator(String username, String password) {
			super();
			this.username = username;
			this.password = password;
		}

		protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
			if (username != null && password != null)
				pw = new javax.mail.PasswordAuthentication(username, password);

			return (pw);
		}
	}

	private MyAuthenticator authenticator = null;
    
	public KMailer() {
	}

	public KMailer(String mailhost) {
		setMailhost(mailhost);
	}

	/* ----------------------  Object Getters   ------------------------- */
	public String getMailhost() {
		return (mailhost);
	}

	public String getUsername() {
		return (username);
	}

	public String getPassword() {
		return (password);
	}

	public String getFrom() {
		return (from);
	}
    
	public String getSender() {
		return (sender);
	}
    
	public String[] getReplyTo() {
		return replyTo;
	}
    

	public String[] getTo() {
		return to;
	}

	public String[] getCC() {
		return cc;
	}

	public String[] getBCC() {
		return bcc;
	}

	public String getSubject() {
		return subject;
	}

	public String getTextBody() {
        return textBody;
	}
    
	public String getHtmlBody() {
        return htmlBody;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public boolean isRichText() {
		return isRichText;
	}

	public boolean isHTML() {
		return isHTML;
	}

	public List<File> getFiles() {
		return files;
	}

	public List<URL> getURLs() {
		return urls;
	}

	public List<KByteArrayDataSource> getByteArrays() {
		return (byteArrays);
	}

    public String toString() {
        /*
        String s = "Host: " + mailhost + "\n";
        s += "From: " + from + "\n";
        s += "Sender: " + sender + "\n";
        s += "ReplyTo: " + KStringUtil.toJson(replyTo) + "\n";
        s += "To: " + KStringUtil.toJson(to) + "\n";
        s += "Cc: " + KStringUtil.toJson(cc) + "\n";
        s += "Bcc: " + KStringUtil.toJson(bcc) + "\n";
        s += "Subject: " + subject + "\n";
        s += "Text Body: " + textBody + "\n";
        s += "Html Body: " + htmlBody + "\n";
        */
        
        String s = null;
        Session session = Session.getInstance(new Properties(), null);
		MimeMessage mimeMessage;
		try {
			mimeMessage = getMimeMessage(session);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			mimeMessage.writeTo(os);
			s = os.toString();
		} catch (IOException | MessagingException e) {
            logger.error(e);
		}
        
        return s;
    }

	/* ----------------------   Setters   ------------------------- */

	public void setMailhost(String mailhost)
	{
		this.mailhost = mailhost;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public void setFrom(String from) {
		setFrom(from, false);
	}

	public void setFrom(String from, boolean multiple) {
		if (multiple) {
			String[] addresses = from.split(",");
			from = addresses[0].trim();
		}
        
		this.from = from;
	}
    

    
	public void setSender(String sender) {
		this.sender = sender;
	}
    
	public void setReplyTo(String[] replyTo) {
		this.replyTo = replyTo;
	}
    
	public void setReplyTo(String replyTo) {
		this.replyTo = null;
        if (replyTo != null) {
        	this.replyTo = new String[]{replyTo};
        }
	}

	public void setTo(String[] to) {
		this.to = to;
	}
    
	public void setTo(String to) {
        this.to = null;
        if (to != null) {
        	this.to = new String[]{to};
        }
	}

	public void setCc(String[] cc) {
		this.cc = cc;
	}

	public void setCc(String cc) {
        this.cc = null;
        if (cc != null) {
        	this.cc = new String[]{cc};
        }
	}
    
	public void setBcc(String[] bcc) {
		this.bcc = bcc;
	}
    
	public void setBcc(String bcc) {
        this.bcc = null;
        if (bcc != null) {
        	this.bcc = new String[]{bcc};
        }
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setTextBody(String textBody) {
		this.textBody = textBody;
	}
    
	public void setHtmlBody(String htmlBody) {
		this.htmlBody = htmlBody;
	}

	public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
	}

	public void setRichText(boolean isRichText) {
		this.isRichText = isRichText;
	}

	public void setHTML(boolean isHTML) {
		this.isHTML = isHTML;
	}

	/* ----------------------   add attachments ------------------------- */
	public void addFile(String path) {
		addFile(new File(path));
	}

	public void addFile(File file) {
		if (files == null)
			files = new ArrayList<File>();

		files.add(file);
	}

	public void addURL(String url) throws MalformedURLException {
		addURL(new URL(url));
	}

	public void addURL(URL url) {
		if (urls == null)
			urls = new ArrayList<URL>();

		urls.add(url);
	}

	public void addByteArray(String name, byte[] data, String contentType) {
		KByteArrayDataSource byteArray = 
						new KByteArrayDataSource(data, contentType);
		byteArray.setName(name);
		addByteArray(byteArray);
	}

	public void addByteArray(KByteArrayDataSource byteArray) {
		if (byteArrays == null)
			byteArrays = new ArrayList<KByteArrayDataSource>();

		byteArrays.add(byteArray);
	}
    
    /*
	public static String encode(String s) {
		try {
			s = MimeUtility.encodeText(s);
		} catch (UnsupportedEncodingException e) {
            logger.error(e);
		}
        return s;
	}
    */
    
	public static String toEncodedAddress(String s) {
        String address = null;
        try {
        	InternetAddress[] inetAddresses = parseAddress(s);
            address = InternetAddress.toString(inetAddresses);
        } catch (Exception e) {
        	logger.error(e);
        }
        return address;
	}
    
    // for some reason InternetAddress.parse() does not encode the name if the email
	// is formatted as "Name <user@email.com>".  It only encodes the name if setPersonal()
	// is explicitly called with a value.  
	public static InternetAddress[] parseAddress(String s) 
			throws AddressException, UnsupportedEncodingException {
		InternetAddress[] addresses = InternetAddress.parse(s, false);
        List<InternetAddress> addressList = new ArrayList<InternetAddress>();
        for (InternetAddress address : addresses) {
        	if (address.getPersonal() != null) {
        		address.setPersonal(address.getPersonal());
        	}
        	addressList.add(address);
        }
        return addressList.toArray(addresses);
	}
    
	public static InternetAddress[] parseAddresses(String[] emails) 
			throws AddressException, UnsupportedEncodingException {
		List<InternetAddress> addresses = new ArrayList<InternetAddress>();
        for (String s: emails) {
            addresses.addAll(Arrays.asList(parseAddress(s)));
        }
        return addresses.toArray(new InternetAddress[0]);
	}
    
	public MimeMessage getMimeMessage(Session session) 
			throws AddressException, MessagingException, UnsupportedEncodingException {
        Properties props = session.getProperties();
		if (returnPath != null) {
			props.put("mail.smtp.from", returnPath);
		}

		MimeMessage msg = new MimeMessage(session);
        
		// set fields
        if (sender != null) {
        	msg.setSender(parseAddress(sender)[0]);
        }
        
		msg.setFrom(parseAddress(from)[0]);
        
        if (to != null) {
            for (String s : to) {
            	msg.addRecipients(Message.RecipientType.TO, parseAddress(s));
            }
        }
        
		if (cc != null) {
            for (String s : cc) {
            	msg.addRecipients(Message.RecipientType.CC, parseAddress(s));
            }
		}

		if (bcc != null) {
            for (String s : bcc) {
            	msg.addRecipients(Message.RecipientType.BCC, parseAddress(s));
            }
		}
        
        if (replyTo != null) {
            msg.setReplyTo(parseAddresses(replyTo));
        }

		msg.setSubject(subject);


		// attach any files
		if (files != null || urls != null || byteArrays != null || htmlBody != null) {
			MimeMultipart mp = new MimeMultipart();
            
            if (textBody != null || htmlBody != null) {
            	MimeBodyPart altPart = new MimeBodyPart();
            	MimeMultipart alt = new MimeMultipart("alternative");
            	if (textBody != null) {
					MimeBodyPart part = new MimeBodyPart();
                    part.setText(textBody, "UTF-8");
					alt.addBodyPart(part);
            	}

            	if (htmlBody != null) {
					MimeBodyPart part = new MimeBodyPart();
                    part.setText(htmlBody, "UTF-8", "html");
					alt.addBodyPart(part);
            	}
                altPart.setContent(alt);
                mp.addBodyPart(altPart);
            }
            

			if (files != null) {
				for (File file : files) {
					FileDataSource source = new FileDataSource(file);
					MimeBodyPart part = new MimeBodyPart();
					part.setDataHandler(new DataHandler(source));
					part.setFileName(file.getName());
					mp.addBodyPart(part);
				}
			}

			if (urls != null) {
				for (URL url : urls) {
					URLDataSource source = new URLDataSource(url);
					MimeBodyPart part = new MimeBodyPart();
					part.setDataHandler(new DataHandler(source));
					part.setFileName(url.getFile());
					mp.addBodyPart(part);
				}
			}

			if (byteArrays != null) {
				for (KByteArrayDataSource source : byteArrays) {
					MimeBodyPart part = new MimeBodyPart();
					part.setDataHandler(new DataHandler(source));
					part.setFileName(source.getName());
					mp.addBodyPart(part);
				}
			}
	
			msg.setContent(mp);	
		} else {
			// attach body without any other attachments
			if (textBody != null) {
				msg.setText(textBody);
			} 
			//msg.setDataHandler(new DataHandler(htmlBody, "text/html"));
		}

		// set header info
		msg.setHeader("X-Mailer", "KonaMailer");
		msg.setSentDate(new Date());
        return msg;
	}
	
	/* ----------------------   Send Email   ------------------------- */
	public static MimeMessage send(MimeMessage message) throws KMailerException {
        try {
        	Transport.send(message);
            return message;
        } catch (Exception e) {
            KMailerException ex = new KMailerException("Error sending email:\n" + message, e);
            ex.setMimeMessage(message);
            throw ex;
		}
	}
    
	public MimeMessage send() throws KMailerException {
		MimeMessage message = null;

		if (from == null)
			throw new NullPointerException("From: address is null");

		if (to == null)
			throw new NullPointerException("To: address is null");

		try {
			Properties props = new Properties();

			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.host", mailhost);

			// added to fix firewall problem??
			props.put("mail.smtp.localhost", "localhost.localdomain");
			// get Session object
			if (username != null && password != null) {
				props.put("mail.user", username);
				props.put("mail.smtp.auth", "true");	
				authenticator = new MyAuthenticator(username, password);
			}
	        
            /*
			if (returnPath != null) {
				props.put("mail.smtp.from", returnPath);
			}
            */

			Session session = Session.getInstance(props, authenticator);
            message = getMimeMessage(session);
			Transport.send(message);
            return message;
		}
		catch (Exception e) {
			String header = "From: " + from + "\n";
			header += "To: " + KStringUtil.toJson(to) + "\n";
            
			if (replyTo != null)
				header += "ReplyTo: " + KStringUtil.toJson(replyTo) + "\n";
            
			if (returnPath != null)
				header += "ReturnPath: " + KStringUtil.toJson(returnPath) + "\n";
            
			if (cc != null)
				header += "Cc: " + KStringUtil.toJson(cc) + "\n";

			if (bcc != null)
				header += "Bcc: " + KStringUtil.toJson(bcc) + "\n";

			if (subject != null)
				header += "Subject: " + subject + "\n";

			logger.error("Error sending email:\n" + header , e);
            KMailerException ex = new KMailerException("Error sending email:\n" + 
					header + "\n" + e.getMessage(), e);
            ex.setMimeMessage(message);
            throw ex;
		}
	}

	public String getReturnPath() {
		return returnPath;
	}

	public void setReturnPath(String returnPath) {
		this.returnPath = returnPath;
	}
}
