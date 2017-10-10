package com.common.net;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;

import com.common.uitl.Constant;
import com.common.uitl.LogUtil;
import com.common.uitl.NetTool;
import com.common.uitl.StringUtils;

/**
 * http请求工具，不直接调用
 * 
 * @author:wangzhengyun 2012-9-11
 */
public class HttpRequester {

	final static String ENCODE = "UTF-8";
	final static int TIME_OUT = 1000 * 60;
	final static String tag = "HttpRequester";

	final static String exception_url_empty = "URL 为空";
	final static String exception_param_empty = "参数 为空";
	final static String exception_request_time_out = "请求超时";
	final static String exception_getway_time_out = "网关超时";
	final static String exception_inner_error = "内部错误";
	final static String exception_getway_error = "网关错误";

	public static final int APN_NOTSET = 0;
	public static final int APN_CMWAP = 1; // 移动2G的wap接入10.0.0.172
	public static final int APN_CMNET = 2; // 移动2G的net接入
	public static final int APN_UNIWAP = 3; // 联通2G的wap接入
	public static final int APN_UNINET = 4; // 联通2G的net接入
	public static final int APN_CTWAP = 5; // 电信2G的wap接入10.0.0.200
	public static final int APN_CTNET = 6; // 电信2G的net接入
	public static final int APN_3GWAP = 7; // 电信3G的wap接入

	private static String mApnName; // 接入点类型名称
	public static int mApn; // 接入点类型

	public static void setApn(String apnName) {
		mApnName = apnName;
		if (apnName == null || apnName.length() == 0) {
			mApn = APN_NOTSET;
			return;
		}
		apnName = apnName.toLowerCase();
		if ("cmwap".equals(apnName)) {
			mApn = APN_CMWAP;
		} else if ("uniwap".equals(apnName)) {
			mApn = APN_UNIWAP;
		} else if ("ctwap".equals(apnName)) {
			mApn = APN_CTWAP;
		} else if ("3gwap".equals(apnName)) {
			mApn = APN_3GWAP;
		} else {
			mApn = APN_NOTSET;
		}
	}

	/**
	 * get方法取得参数
	 * 
	 * @deprecated 本应用全部使用 post请求,如果需要使用get请求,请修改下面的方法后使用
	 * @param path
	 * @param paramMap
	 * @throws UnsupportedEncodingException
	 *             void 下午03:39:30 2011
	 */
	@Deprecated
	public static String httpGet(Context context, String path, Map<String, String> paramMap) throws NetException, Exception {

		StringBuffer result = new StringBuffer();
		StringBuffer paramSbf = new StringBuffer();

		if (null != paramMap) {
			paramMap.putAll(NetTool.getCommonParam(context));
			// 组装参数
			for (Map.Entry<String, String> entry : paramMap.entrySet()) {

				String name = entry.getKey();
				String value = entry.getValue();
				if (StringUtils.isEmpty(value)) {
					value = "";
				}
				paramSbf.append(name + "=" + URLEncoder.encode(value, ENCODE) + "&");
			}
		} else {
			throw new NetException(-1, exception_param_empty);
		}
		// 组装url
		if (StringUtils.isEmpty(path)) {
			throw new NetException(-1, exception_url_empty);
		} else {
			// 检测url
			String paramString = paramSbf.toString();
			if (path.endsWith("?")) {
				path = path + paramSbf.toString().substring(0, paramString.length());
			} else {
				path = path + "?" + paramSbf.toString().substring(0, paramString.length());
			}
			// 网络请求

			result = doHttpRequest(context, path, null);
		}
		return result.toString();
	}

	/**
	 * httpPost参数,适用tralveksk相关接口
	 * 
	 * @param path
	 * @param paramMap
	 * @return
	 * @throws Exception
	 *             String 下午05:53:46 2011
	 */
	public static InputStream httpPost(Context context, String path, Map<String, String> paramMap) throws NetException, Exception {
		InputStream netInputStream = null;
		if (null == paramMap) {
			throw new NetException(-1, exception_param_empty);
		}

		// 组装url
		if (StringUtils.isEmpty(path)) {
			throw new NetException(-1, exception_url_empty);
		} else {
			StringBuffer xmlBuffer = new StringBuffer();
			xmlBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			xmlBuffer.append("<request>");
			xmlBuffer.append("<common>");
			// 添加公共参数
			for (Entry<String, String> entry : NetTool.getCommonParam(context).entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (StringUtils.isEmpty(value)) {
					value = "";
				}
				xmlBuffer.append("<" + key.trim() + ">" + value + "</" + key.trim() + ">");
			}
			xmlBuffer.append("</common>");

			xmlBuffer.append("<customized>");
			// 添加接口的参数参数

			for (Entry<String, String> entry : paramMap.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (StringUtils.isEmpty(value)) {
					value = "";
				}
				xmlBuffer.append("<" + key.trim() + ">" + value + "</" + key + ">");
			}

			xmlBuffer.append("</customized>");
			xmlBuffer.append("</request>");
			// 添加请求参数

			netInputStream = doHttpRequestXML(context, path, xmlBuffer.toString());
		}
		return netInputStream;
	}

	/**
	 * httpPost for special case which cannot be express by simple hashmap
	 * 
	 * @param path
	 * @param paramString
	 * @return
	 * @throws Exception
	 * 
	 */
	public static InputStream httpPost(Context context, String path, String paramStr) throws NetException, Exception {
		InputStream netInputStream = null;
		if (null == paramStr) {
			throw new NetException(-1, exception_param_empty);
		}

		// 组装url
		if (StringUtils.isEmpty(path)) {
			throw new NetException(-1, exception_url_empty);
		} else {
			StringBuffer xmlBuffer = new StringBuffer();
			xmlBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			xmlBuffer.append("<request>");
			xmlBuffer.append("<common>");
			// 添加公共参数
			for (Entry<String, String> entry : NetTool.getCommonParam(context).entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (StringUtils.isEmpty(value)) {
					value = "";
				}
				xmlBuffer.append("<" + key.trim() + ">" + value + "</" + key.trim() + ">");
			}
			xmlBuffer.append("</common>");

			xmlBuffer.append("<customized>");

			// 添加接口的参数参数
			xmlBuffer.append(paramStr);

			xmlBuffer.append("</customized>");
			xmlBuffer.append("</request>");
			// 添加请求参数

			netInputStream = doHttpRequestXML(context, path, xmlBuffer.toString());
		}
		return netInputStream;
	}

	/**
	 * 下载城市数据
	 * 
	 * @param url
	 * @return
	 */
	public static InputStream downLoadCityData(Context context, String uri) {
		InputStream in = null;
		try {
			URL url = new URL(uri);
			HttpURLConnection conn = null;
			// 查看apn类型
			String typeName = NetTool.getNetType(context);
			setApn(typeName);
			switch (mApn) {
			case APN_CMWAP: {
				try {
					Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
					conn = (HttpURLConnection) url.openConnection(proxy);
				} catch (Exception e) {
					LogUtil.e(Constant.TAG, "apn_wamp fail");
					e.printStackTrace();
				}
			}
				break;
			case APN_3GWAP: {
				try {
					Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
					conn = (HttpURLConnection) url.openConnection(proxy);
				} catch (Exception e) {
					LogUtil.e(Constant.TAG, "APN_3GWAP fail");
					e.printStackTrace();
				}
			}
				break;
			case APN_UNIWAP: {
				try {
					Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
					conn = (HttpURLConnection) url.openConnection(proxy);
				} catch (Exception e) {
					LogUtil.e(Constant.TAG, "APN_3GWAP fail");
					e.printStackTrace();
				}
			}
				break;
			case APN_CTWAP: {
				try {
					Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.200", 80));
					conn = (HttpURLConnection) url.openConnection(proxy);
				} catch (Exception e) {
					LogUtil.e(Constant.TAG, "APN_CTWAP fail");
					e.printStackTrace();
				}
			}
				break;
			default:
				conn = (HttpURLConnection) url.openConnection();
				break;
			}
			// 更具不通的apn类型，设置不通代理
			conn.setRequestProperty("Content-Type", "application/xml");
			conn.setConnectTimeout(TIME_OUT);
			conn.setReadTimeout(TIME_OUT);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 更具不通的apn类型，设置不通代理
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Charset", ENCODE);
			conn.connect();
			if (null != conn) {
				StringBuffer xmlBuffer = new StringBuffer();
				xmlBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				xmlBuffer.append("<request>");
				xmlBuffer.append("<common>");
				// 添加公共参数
				for (Entry<String, String> entry : NetTool.getCommonParam(context).entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					xmlBuffer.append("<" + key + ">" + value + "</" + key + ">");
				}
				xmlBuffer.append("</common>");
				xmlBuffer.append("<customized>");
				xmlBuffer.append("</customized>");
				xmlBuffer.append("</request>");
				conn.getOutputStream().write(xmlBuffer.toString().getBytes());
			}
			int responseCode = conn.getResponseCode();
			if (200 == responseCode) {
				in = conn.getInputStream();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return in;
	}

	/**
	 * 下载新的app
	 * 
	 * @param downloadUrl
	 * @return
	 */
	public static InputStream doHttpDownLoad(Context context, String downloadUrl) {

		InputStream in = null;
		try {
			URL url = new URL(downloadUrl);
			HttpURLConnection conn = null;
			String typeName = NetTool.getNetType(context);
			setApn(typeName);
			switch (mApn) {
			case APN_CMWAP: {
				try {
					Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
					conn = (HttpURLConnection) url.openConnection(proxy);
				} catch (Exception e) {
					LogUtil.e(Constant.TAG, "apn_wamp fail");
					e.printStackTrace();
				}
			}
				break;
			case APN_3GWAP: {
				try {
					Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
					conn = (HttpURLConnection) url.openConnection(proxy);
				} catch (Exception e) {
					LogUtil.e(Constant.TAG, "APN_3GWAP fail");
					e.printStackTrace();
				}
			}
				break;
			case APN_UNIWAP: {
				try {
					Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
					conn = (HttpURLConnection) url.openConnection(proxy);
				} catch (Exception e) {
					LogUtil.e(Constant.TAG, "APN_3GWAP fail");
					e.printStackTrace();
				}
			}
				break;
			case APN_CTWAP: {
				try {
					Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.200", 80));
					conn = (HttpURLConnection) url.openConnection(proxy);
				} catch (Exception e) {
					LogUtil.e(Constant.TAG, "APN_CTWAP fail");
					e.printStackTrace();
				}
			}
				break;
			default:
				conn = (HttpURLConnection) url.openConnection();
				break;
			}
			// 更具不通的apn类型，设置不通代理
			conn.setRequestProperty("Content-Type", "application/xml");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setConnectTimeout(TIME_OUT);
			conn.setReadTimeout(TIME_OUT);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 更具不通的apn类型，设置不通代理
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Charset", ENCODE);
			conn.connect();
			if (null != conn) {
				StringBuffer xmlBuffer = new StringBuffer();
				xmlBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				xmlBuffer.append("<request>");
				xmlBuffer.append("<common>");
				// 添加公共参数
				for (Entry<String, String> entry : NetTool.getCommonParam(context).entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					xmlBuffer.append("<" + key + ">" + value + "</" + key + ">");
				}
				xmlBuffer.append("</common>");
				xmlBuffer.append("<customized>");
				xmlBuffer.append("</customized>");
				xmlBuffer.append("</request>");
				conn.getOutputStream().write(xmlBuffer.toString().getBytes(ENCODE));
			}
			int responseCode = conn.getResponseCode();
			if (200 == responseCode) {
				in = conn.getInputStream();
			} else {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return in;

	}

	/**
	 * skyworth post请求
	 * 
	 * @param context
	 * @param path
	 * @param paramMap
	 * @return
	 * @throws IOException
	 * @throws NetException
	 * @throws Exception
	 */
	public static InputStream doSkyWorthPost(Context context, String path, HashMap<String, String> paramMap) throws IOException,
			NetException, Exception {
		byte[] data = null;
		if (null != paramMap && !paramMap.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (Entry<String, String> entry : paramMap.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (!StringUtils.isEmpty(value)) {
					sb.append(key + "=" + value + "&");
				} else {
					sb.append(key + "=" + "&");
				}
			}
			data = sb.toString().getBytes();
			LogUtil.e(Constant.TAG, path + sb.toString());
		}
		return doHttpRequestPost(context, path, data);
	}

	/**
	 * post请求返回json数据,适用skyworth
	 * 
	 * @param context
	 * @param path
	 * @param json
	 * @return
	 * @throws IOException
	 * @throws NetException
	 * @throws Exception
	 */
	public static InputStream doHttpRequestPost(Context context, String path, byte[] data) throws IOException, NetException, Exception {
		URL url = new URL(path);
		HttpURLConnection conn = null;
		// 查看apn类型
		String typeName = NetTool.getNetType(context);
		setApn(typeName);
		LogUtil.i(Constant.TAG, "apn:" + typeName + " url:" + path);
		conn = createProxyConnection(mApn, url);
		conn.setRequestProperty("connection", "Keep-Alive");
		conn.setConnectTimeout(TIME_OUT);
		conn.setReadTimeout(TIME_OUT);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		if (!StringUtils.isEmpty(sessionid)) {
			conn.setRequestProperty("Cookie", sessionid);
		}
		// 更具不通的apn类型，设置不通代理
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Charset", ENCODE);
		conn.connect();
		if (null != data && null != conn) {
			OutputStream os = conn.getOutputStream();
			os.write(data);
		}
		return createNetInputStream(conn);
	}

	/**
	 * 使用 postXML形式，该方法适用于travelsky的网络接口
	 * 
	 * @param path
	 * @param xml
	 * @return
	 */
	public static InputStream doHttpRequestXML(Context context, String path, String xml) throws NetException, Exception {
		URL url = new URL(path);
		HttpURLConnection conn = null;
		// 查看apn类型
		String typeName = NetTool.getNetType(context);
		setApn(typeName);
		LogUtil.i(Constant.TAG, "apn:" + typeName + " url:" + path);
		conn = createProxyConnection(mApn, url);
		conn.setRequestProperty("Content-Type", "application/xml;charset=UTF-8");
		conn.setRequestProperty("connection", "Keep-Alive");
		conn.setConnectTimeout(TIME_OUT);
		conn.setReadTimeout(TIME_OUT);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		// 更具不通的apn类型，设置不通代理
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Charset", ENCODE);
		conn.connect();
		if (!StringUtils.isEmpty(xml)) {
			OutputStream os = conn.getOutputStream();
			os.write(xml.getBytes());
		}
		return createNetInputStream(conn);
	}

	public static String sessionid = "";

	/**
	 * 返回数据流
	 * 
	 * @param conn
	 * @return
	 * @throws IOException
	 * @throws NetException
	 */
	public static InputStream createNetInputStream(HttpURLConnection conn) throws IOException, NetException {
		int responseCode = conn.getResponseCode();
		switch (responseCode) {
		case 200: {
			// 正常
			Map map = conn.getHeaderFields();
			List<String> list = (List) map.get("Set-Cookie");
			if (null != list && !list.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				for (String str : list) {
					sessionid = builder.append(str).toString();
				}
			}

			// 保存session信息
			InputStream ins = conn.getInputStream();
			return ins;
		}
		case 408: {
			// 请求超时
			throw new NetException(408, exception_request_time_out);
		}
		case 504: {
			// 网关超时
			throw new NetException(504, exception_getway_time_out);
		}
		case 500: {
			// 内部错误
			throw new NetException(500, exception_inner_error);
		}
		case 502: {
			// 网关错误
			throw new NetException(502, exception_getway_error);
		}
		default:
			throw new NetException(responseCode, "网络异常 代码:" + responseCode);
		}
	}

	/**
	 * 生成带有代理的Connection
	 * 
	 * @param mApn
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static HttpURLConnection createProxyConnection(int mApn, URL url) throws IOException {
		HttpURLConnection conn = null;
		switch (mApn) {
		case APN_CMWAP: {
			try {
				Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
				conn = (HttpURLConnection) url.openConnection(proxy);
			} catch (Exception e) {
				LogUtil.e(Constant.TAG, "apn_wamp fail");
				e.printStackTrace();
			}
		}
			break;
		case APN_3GWAP: {
			try {
				Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
				conn = (HttpURLConnection) url.openConnection(proxy);
			} catch (Exception e) {
				LogUtil.e(Constant.TAG, "APN_3GWAP fail");
				e.printStackTrace();
			}
		}
			break;
		case APN_UNIWAP: {
			try {
				Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
				conn = (HttpURLConnection) url.openConnection(proxy);
			} catch (Exception e) {
				LogUtil.e(Constant.TAG, "APN_3GWAP fail");
				e.printStackTrace();
			}
		}
			break;
		case APN_CTWAP: {
			try {
				Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.200", 80));
				conn = (HttpURLConnection) url.openConnection(proxy);
			} catch (Exception e) {
				LogUtil.e(Constant.TAG, "APN_CTWAP fail");
				e.printStackTrace();
			}
		}
			break;
		default:
			conn = (HttpURLConnection) url.openConnection();
			break;

		}
		return conn;
	}

	/**
	 * data为null就是get请求
	 * 
	 * @param path
	 * @param data
	 * @return String 上午09:27:54 2011
	 */
	public static StringBuffer doHttpRequest(Context context, String path, byte[] data) throws NetException, Exception {
		StringBuffer result = new StringBuffer();
		URL url = new URL(path);
		HttpURLConnection conn = null;
		// 查看apn类型
		String typeName = NetTool.getNetType(context);
		setApn(typeName);
		LogUtil.i(Constant.TAG, "apn:" + typeName + " url:" + path);
		switch (mApn) {
		case APN_CMWAP: {
			try {
				Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
				conn = (HttpURLConnection) url.openConnection(proxy);
			} catch (Exception e) {
				LogUtil.e(Constant.TAG, "apn_wamp fail");
				e.printStackTrace();
			}
		}
			break;
		case APN_3GWAP: {
			try {
				Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
				conn = (HttpURLConnection) url.openConnection(proxy);
			} catch (Exception e) {
				LogUtil.e(Constant.TAG, "APN_3GWAP fail");
				e.printStackTrace();
			}
		}
			break;
		case APN_UNIWAP: {
			try {
				Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
				conn = (HttpURLConnection) url.openConnection(proxy);
			} catch (Exception e) {
				LogUtil.e(Constant.TAG, "APN_3GWAP fail");
				e.printStackTrace();
			}
		}
			break;
		case APN_CTWAP: {
			try {
				Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.200", 80));
				conn = (HttpURLConnection) url.openConnection(proxy);
			} catch (Exception e) {
				LogUtil.e(Constant.TAG, "APN_CTWAP fail");
				e.printStackTrace();
			}
		}
			break;
		default:
			conn = (HttpURLConnection) url.openConnection();
			break;
		}
		conn.setConnectTimeout(TIME_OUT);
		conn.setDoOutput(true);
		conn.setUseCaches(false);

		// 更具不通的apn类型，设置不通代理

		if (null == data) {
			conn.setRequestMethod("GET");
		} else {
			conn.setRequestMethod("POST");
		}
		conn.setRequestProperty("Charset", ENCODE);

		if (null != data) {
			OutputStream os = conn.getOutputStream();
			os.write(data);
		}

		int responseCode = conn.getResponseCode();
		switch (responseCode) {
		case 200: {
			// 正常
			InputStream ins = conn.getInputStream();
			BufferedInputStream fin = new BufferedInputStream(ins);
			int b = -1;
			byte buf[] = new byte[512];

			String tempStr = null;
			while ((b = fin.read(buf)) != -1) {
				tempStr = new String(buf, 0, buf.length);
				result.append(tempStr);
			}
			fin.close();
			ins.close();
			return result;
		}
		case 408: {
			// 请求超时
			throw new NetException(408, exception_request_time_out);
		}
		case 504: {
			// 网关超时
			throw new NetException(504, exception_getway_time_out);
		}
		case 500: {
			// 内部错误
			throw new NetException(500, exception_inner_error);
		}
		case 502: {
			// 网关错误
			throw new NetException(502, exception_getway_error);
		}
		default:
			throw new NetException(responseCode, "网络异常 代码:" + responseCode);
		}
	}

	/**
	 * 文件上传
	 * 
	 * @param actionUrl
	 * @param params
	 * @param files
	 * @return String 上午11:24:20 2011
	 */
	public static String postUpload(String actionUrl, Map<String, String> params, FormFile[] files) throws Exception {

		String BOUNDARY = "---------7d4a6d158c9"; // 数据分隔线

		String MULTIPART_FORM_DATA = "multipart/form-data";

		URL url = new URL(actionUrl);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setConnectTimeout(TIME_OUT);

		conn.setDoInput(true);// 允许输入

		conn.setDoOutput(true);// 允许输出

		conn.setUseCaches(false);// 不使用Cache

		conn.setRequestMethod("POST");

		conn.setRequestProperty("Connection", "Keep-Alive");

		conn.setRequestProperty("Charset", ENCODE);
		
		conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);

		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容

			sb.append("--");

			sb.append(BOUNDARY);

			sb.append("\r\n");

			sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");

			sb.append(entry.getValue());

			sb.append("\r\n");

		}

		DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());

		outStream.write(sb.toString().getBytes());// 发送表单字段数据

		for (FormFile file : files) {// 发送文件数据

			StringBuilder split = new StringBuilder();

			split.append("--");

			split.append(BOUNDARY);

			split.append("\r\n");

			split.append("Content-Disposition: form-data;name=\"" + file.getParameterName() + "\";filename=\"" + file.getFilname() + "\"\r\n");

			split.append("Content-Type: " + file.getContentType() + "\r\n\r\n");

			outStream.write(split.toString().getBytes());
			
			outStream.write(file.getData(), 0, file.getData().length);

			outStream.write("\r\n".getBytes());

		}

		byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 数据结束标志

		outStream.write(end_data);

		outStream.flush();

		int responseCode = conn.getResponseCode();

		if (200 == responseCode) {
			BufferedInputStream bfi=new BufferedInputStream( conn.getInputStream());
			int ret=-1;
			StringBuilder b = new StringBuilder();
			byte[] buffer=new byte[512];
			while((ret=bfi.read(buffer))!=-1){
				b.append(new String(buffer,0,ret));
			}
			bfi.close();
			outStream.close();
			conn.disconnect();
			return b.toString();
		} else {
			return null;
		}

	}

	public static String post(String actionUrl, Map<String, String> params, FormFile file) throws Exception {

		return postUpload(actionUrl, params, new FormFile[] { file });

	}

	public static String post(String actionUrl, Map<String, String> params) {

		HttpPost httpPost = new HttpPost(actionUrl);

		List<NameValuePair> list = new ArrayList<NameValuePair>();

		for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容

			list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));

		}

		try {

			httpPost.setEntity(new UrlEncodedFormEntity(list, ENCODE));

			HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {

				return EntityUtils.toString(httpResponse.getEntity());
			}

		} catch (Exception e) {

			throw new RuntimeException(e);

		}

		return null;

	}
}
