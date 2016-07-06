package com.config.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.os.Environment;
import android.util.Xml;

import com.utils.BrInfo;
import com.utils.GESHIHUA;

public class Config {
	private BrInfo brInfo;
	private BrInfo fh_data;
	private String login_Name;
	private String bf_Time;

	private String readId;
	private String path;
	private String fileName;

	public Config(BrInfo brInfo, String login_Name, String bf_Time) {
		this.brInfo = brInfo;
		this.login_Name = login_Name;
		this.bf_Time = bf_Time;
	}

	public Config(String readId) {
		this.readId = readId;
	}
	
	public Config(){}

	public String writeXmlToLocal() {
		XmlSerializer serializer = Xml.newSerializer();
		try {
			if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				//�����ļ���
				File sdcardDir = Environment.getExternalStorageDirectory();
				path = sdcardDir.getPath() + GESHIHUA.SD_XML_PATH_CK;
				File path1 = new File(path);
				if(!path1.exists()) {
					//�������ڴ���Ŀ¼
					path1.mkdir();
				}
				
				File f = new File(GESHIHUA.SD_XML_PATH + brInfo.getId() + ".xml");
				FileOutputStream fos = new FileOutputStream(f);
				// �������л����������λ�úͱ���
				serializer.setOutput(fos, "utf-8");
				// д��ʼ <!--?xml version='1.0' encoding='utf-8' standalone='yes' ?-->
				serializer.startDocument("utf-8", true);
				serializer.startTag(null, "������Ϣ����");
				serializer.startTag(null, "������Ϣ");

				serializer.startTag(null, "Id");
				serializer.text(brInfo.getId());
				serializer.endTag(null, "Id");

				serializer.startTag(null, "Name");
				serializer.text(brInfo.getName());
				serializer.endTag(null, "Name");

				serializer.startTag(null, "Age");
				serializer.text(brInfo.getAge());
				serializer.endTag(null, "Age");

				serializer.startTag(null, "Sex");
				serializer.text(brInfo.getSex());
				serializer.endTag(null, "Sex");

				serializer.startTag(null, "Lxfs");
				serializer.text(brInfo.getLxfs());
				serializer.endTag(null, "Lxfs");

				serializer.startTag(null, "Brzz");
				serializer.text(brInfo.getBrzz());
				serializer.endTag(null, "Brzz");

				serializer.startTag(null, "Yz");
				serializer.text(brInfo.getYz());
				serializer.endTag(null, "Yz");

				serializer.startTag(null, "Zybch");
				serializer.text(brInfo.getZybch());
				serializer.endTag(null, "Zybch");

				serializer.startTag(null, "Hyzk");
				serializer.text(brInfo.getHyzk());
				serializer.endTag(null, "Hyzk");

				serializer.startTag(null, "Yxck");
				serializer.text(brInfo.getYxck());
				serializer.endTag(null, "Yxck");

				serializer.startTag(null, "Tssm");
				serializer.text(brInfo.getTssm());
				serializer.endTag(null, "Tssm");

				serializer.startTag(null, "Shz");
				serializer.text(brInfo.getShz());
				serializer.endTag(null, "Shz");

				serializer.startTag(null, "ShTime");
				serializer.text(brInfo.getShtime());
				serializer.endTag(null, "ShTime");

				serializer.startTag(null, "���ݲ�����");
				serializer.text(login_Name);
				serializer.endTag(null, "���ݲ�����");

				serializer.startTag(null, "����ʱ��");
				serializer.text(bf_Time);
				serializer.endTag(null, "����ʱ��");

				serializer.endTag(null, "������Ϣ");
				serializer.endTag(null, "������Ϣ����");

				serializer.endDocument();

				return "���ݳɹ����뵽" + GESHIHUA.SD_XML_PATH_CK + "�²鿴";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// ʹ��Pull��������ȡ���ص�XML

	public BrInfo parserXmlFromLocal() {
		try {
			File f = new File(GESHIHUA.SD_XML_PATH + readId + ".xml");
			FileInputStream fis = new FileInputStream(f);

			// ���pull����������
			XmlPullParser parser = Xml.newPullParser();
			// ָ���������ļ��ͱ����ʽ
			parser.setInput(fis, "utf-8");
			// ����¼�����
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				// ��õ�ǰ�ڵ������
				String tagName = parser.getName();
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("������Ϣ����".equals(tagName)) {
						System.out.println("��ȡ������Ϣ");
					} else if ("������Ϣ".equals(tagName)) {
						fh_data = new BrInfo();
					} else if ("Id".equals(tagName)) {
						fh_data.setId(parser.nextText());
					} else if ("Name".equals(tagName)) {
						fh_data.setName(parser.nextText());
					} else if ("Age".equals(tagName)) {
						fh_data.setAge(parser.nextText());
					} else if ("Sex".equals(tagName)) {
						fh_data.setSex(parser.nextText());
					} else if ("Lxfs".equals(tagName)) {
						fh_data.setLxfs(parser.nextText());
					} else if ("Brzz".equals(tagName)) {
						fh_data.setBrzz(parser.nextText());
					} else if ("Yz".equals(tagName)) {
						fh_data.setYz(parser.nextText());
					} else if ("Zybch".equals(tagName)) {
						fh_data.setZybch(parser.nextText());
					} else if ("Hyzk".equals(tagName)) {
						fh_data.setHyzk(parser.nextText());
					} else if ("Yxck".equals(tagName)) {
						fh_data.setYxck(parser.nextText());
					} else if ("Tssm".equals(tagName)) {
						fh_data.setTssm(parser.nextText());
					} else if ("Shz".equals(tagName)) {
						fh_data.setShz(parser.nextText());
					} else if ("ShTime".equals(tagName)) {
						fh_data.setShtime(parser.nextText());
					}
					break;
				default:
					break;
				}
				//�����һ���¼�����
				eventType = parser.next();
			}
			return fh_data;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}
	
	public BrInfo parserXmlFromLocal(String fileName) {
		try {
			File f = new File(GESHIHUA.SD_XML_PATH + fileName);
			FileInputStream fis = new FileInputStream(f);

			// ���pull����������
			XmlPullParser parser = Xml.newPullParser();
			// ָ���������ļ��ͱ����ʽ
			parser.setInput(fis, "utf-8");
			// ����¼�����
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				// ��õ�ǰ�ڵ������
				String tagName = parser.getName();
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("������Ϣ����".equals(tagName)) {
						System.out.println("��ȡ������Ϣ");
					} else if ("������Ϣ".equals(tagName)) {
						fh_data = new BrInfo();
					} else if ("Id".equals(tagName)) {
						fh_data.setId(parser.nextText());
					} else if ("Name".equals(tagName)) {
						fh_data.setName(parser.nextText());
					} else if ("Age".equals(tagName)) {
						fh_data.setAge(parser.nextText());
					} else if ("Sex".equals(tagName)) {
						fh_data.setSex(parser.nextText());
					} else if ("Lxfs".equals(tagName)) {
						fh_data.setLxfs(parser.nextText());
					} else if ("Brzz".equals(tagName)) {
						fh_data.setBrzz(parser.nextText());
					} else if ("Yz".equals(tagName)) {
						fh_data.setYz(parser.nextText());
					} else if ("Zybch".equals(tagName)) {
						fh_data.setZybch(parser.nextText());
					} else if ("Hyzk".equals(tagName)) {
						fh_data.setHyzk(parser.nextText());
					} else if ("Yxck".equals(tagName)) {
						fh_data.setYxck(parser.nextText());
					} else if ("Tssm".equals(tagName)) {
						fh_data.setTssm(parser.nextText());
					} else if ("Shz".equals(tagName)) {
						fh_data.setShz(parser.nextText());
					} else if ("ShTime".equals(tagName)) {
						fh_data.setShtime(parser.nextText());
					}
					break;
				default:
					break;
				}
				//�����һ���¼�����
				eventType = parser.next();
			}
			return fh_data;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}
	
	

}
