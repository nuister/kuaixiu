package com.litesuits.http.response;

import android.graphics.Bitmap;
import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.parser.DataParser;
import com.litesuits.http.request.Request;

import java.io.File;
import java.io.InputStream;

/**
 * User Facade
 * providing developers with easy access to the results of http(
 * {@link com.litesuits.http.LiteHttpClient#execute(Request) }) response,
 * and with information of exeception,request,charset,etc.
 * @author MaTianyu
 * 2014-1-1下午10:00:42
 */
public interface Response {

	public String getString();

	public byte[] getBytes();

	public <T> T getObject(Class<T> claxx);

	public <T> T getObjectWithMockData(Class<T> claxx, String json);

	public File getFile();

	public Bitmap getBitmap();

	/**
	 * @deprecated
	 */
	public InputStream getInputStream();

	public NameValuePair[] getHeaders();

	public long getContentLength();

	public String getCharSet();

	public Request getRequest();

	public HttpException getException();

	public HttpStatus getHttpStatus();

	public boolean isSuccess();

	public int getTryTimes();

	public int getRedirectTimes();

	public long getUseTime();

	public int getReadedLength();

	public DataParser<?> getDataParser();

    void printInfo();

}
