/*
 * Copyright (C) 2015 LinuxTek, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.mailer;

import javax.mail.internet.MimeMessage;

@SuppressWarnings("serial")
public class KMailerException extends Exception {
    private MimeMessage message = null;
    
	public KMailerException() {
        super();
	}

	public KMailerException(String message) {
		super(message);
	}

	public KMailerException(String message, Throwable cause) {
		super(message, cause);
	}

	public KMailerException(Throwable cause) {
		super(cause);
	}

    public void setMimeMessage(MimeMessage message) {
        this.message = message;
    }
    
	public MimeMessage getMimeMessage() {
        return message;
	}

}
