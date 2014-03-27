package com.baseline.android.frame.logic;

import java.io.Serializable;

import android.content.ContentValues;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="history")
public class BaseMessageModel implements Serializable
{
    /**
     * 消息方向： 发送出去的消息
     */
    public static final int MSGSENDORRECV_SEND = 0;
    
    /**
     * 消息方向： 接收
     */
    public static final int MSGSENDORRECV_RECV = 1;
    
    //=======================================
    
    /**
     * 消息阅读状态: 待发送
     */
    public static final int MSGSTATUS_PREPARE_SEND = 1;
    
    /**
     * 消息阅读状态: 已发送
     */
    public static final int MSGSTATUS_SENDED = 2;
    
    /**
     * 消息阅读状态: 已送达未读
     */
    public static final int MSGSTATUS_SEND_UNREAD = 3;
    
    /**
     * 消息阅读状态: 已读
     */
    public static final int MSGSTATUS_READED = 4;
    
    /**
     * 无状态
     */
    public static final int MSGSTATUS_NO_STATUS = 5;
    
    /**
     * 未读，需发送阅读报告
     */
    public static final int MSGSTATUS_UNREAD_NEED_REPORT = 10;
    
    /**
     * 未读，无需发送阅读报告
     */
    public static final int MSGSTATUS_UNREAD_NO_REPORT = 11;
    
    /**
     * 已读，需发送阅读报告
     */
    public static final int MSGSTATUS_READED_NEED_REPORT = 12;
    
    /**
     * 已读，无需发送阅读报告
     */
    public static final int MSGSTATUS_READED_NO_REPORT = 13;
    
    /**
     * 消息阅读状态: 已发送但是还没有获得回执
     */
    public static final int MSGSTATUS_SENDED_NOREPORT = 14 ;
    
    /**
     * 消息阅读状态: 阻塞状态(多媒体消息正在上传附件，不处理)
     */
    public static final int MSGSTATUS_BLOCK = 100;
    
    /**
     * 消息阅读状态: 发送失败
     */
    public static final int MSGSTATUS_SEND_FAIL = 101;
    
    
    //=============================================
    /**
     * 消息内容类型 ：文本（含图片表情符号）
     */
    public static final int MSGTYPE_TEXT = 0;
    
    /**
     * 消息内容类型 ：图片
     */
    public static final int MSGTYPE_PIC = 1;
    
    /**
     * 消息内容类型 ：音频
     */
    public static final int MSGTYPE_AUDIO = 2;
    
    /**
     * 消息内容类型 :系统提示
     */
    public static final int MSGTYPE_SYSTEM = 3;
    
    /**
     * 消息内容类型：短信
     */
    public static final int MSGTYPE_SMS = 4;
    
    /**
     * 消息内容类型：真实电话
     */
    public static final int MSGTYPE_NORMALCALL = 5;
    
    /**
     * 消息内容类型：请求类型：比如chatroom invite
     */
    public static final int MSGTYPE_REQUEST = 6;
    
    //=============================================
    
    @DatabaseField(generatedId= true)
    private int _id ;
    /**
     * 发送接收消息时，客户端存储时生成的唯一标识：UUID；用于与媒体资源表的对应
     */
    @DatabaseField(index = true)
    private String msgId;
    
    /**
     * 消息序号，发送消息时由FAST生成。接收时为发送方生成的。
     */
    @DatabaseField
    private String msgSequence;
    
    /**
     * 消息发送/接收时间，毫秒级别UTC时间戳
     */
    @DatabaseField
    private String msgTime;
    
    /**
     * 消息内容类型： <br>
     * 0：文本 <br>
     * 1：图片 <br>
     * 2：音频 <br>
     */
    @DatabaseField
    private int msgType;
    
    /**
     * 文本消息内容（图文混排的文本也存放在这里），
     * 注：如果是多媒体消息，需要在多媒体消息表内查询详情
     */
    @DatabaseField
    private String msgContent;
    
    /**
     * 消息方向： <br>
     * 1：发送出去的消息 <br>
     * 2：接收到的消息 <br>
     */
    @DatabaseField
    private int msgSendOrRecv;
    
    /**
     * 当发送时，发送的消息阅读状态： <br>
     * 1：待发送 <br>
     * 2：已发送 <br>
     * 3：已送达未读 <br>
     * 4：已读 <br>
     * 当接收时，收到的消息状态 <br>
     * 10：未读,需发送阅读报告<br>
     * 11：未读,无需发送阅读报告 <br>
     * 12：已读，需发送阅读报告<br>
     * 13：已读，无需发送阅读报告<br>
     * 
     * 100: 阻塞状态(多媒体消息正在上传附件，不处理) <br>
     * 101： 发送失败
     */
    @DatabaseField
    private int msgStatus;
    
    /**
     * 多媒体，主要用是查询消息时
     */
//    private MediaIndexModel mediaIndex;
    /**
     * 用户系统标识，仅用于 "数据迁移"
     */
//    private String userSysId;
    
    //==新增=========================================
    /**
     * 多媒体文件的附件名称，音频文件，或者图片文件本地的路径
     */
    @DatabaseField
    private String msgAttachment;
    
    /**
     * 图片文件的缩略图名称
     */
    @DatabaseField
    private String msgAttachmentSmall;
    
    /**
     * 只针对于音频和视频文件，播放长度
     */
    @DatabaseField
    private int msgPlayTime;
    
    /**
     * 当前登录的用户id
     */
    @DatabaseField
    private String msgOwner;
    
    /**
     * 会话好友的id
     */
    @DatabaseField
    private String msgContactID;
    
    //------------------------------------------------
    
    /**
     * 会话id
     */
    @DatabaseField
    private String sessionid ;
	
	/**
	 * 群组会话的创建者
	 */
    @DatabaseField
	private String groupcreator ;
	/**
	 * 群组会话的参与者
	 */
    @DatabaseField
	private String groupmember ;
	/**
	 * 群组名称
	 */
    @DatabaseField
	private String gname ;
	/**
	 * 群成员的版本
	 */
    @DatabaseField
	private int gversion = -1;
	/**
	 * 对象的手机号
	 */
    @DatabaseField
	private String mobile;
	/**
	 * 对象的xmppid
	 */
    @DatabaseField
	private String friendUserId;
    //------------------------------------------------
    
    
    
    /**
     * 构造方法
     */
    public BaseMessageModel()
    {
        
    }
    


	public String getMsgContactID() {
		return msgContactID;
	}



	public void setMsgContactID(String msgContactID) {
		this.msgContactID = msgContactID;
	}



	public String getMsgAttachment() {
		return msgAttachment;
	}



	public void setMsgAttachment(String msgAttachment) {
		this.msgAttachment = msgAttachment;
	}



	public String getMsgOwner() {
		return msgOwner;
	}



	public void setMsgOwner(String msgOwner) {
		this.msgOwner = msgOwner;
	}



	public String getMsgId()
    {
        return msgId;
    }
    
    public void setMsgId(String msgId)
    {
        this.msgId = msgId;
    }
    
    public String getMsgSequence()
    {
        return msgSequence;
    }
    
    public void setMsgSequence(String msgSequence)
    {
        this.msgSequence = msgSequence;
    }
    
    public String getMsgTime()
    {
        return msgTime;
    }
    
    public void setMsgTime(String msgTime)
    {
        this.msgTime = msgTime;
    }
    
    public int getMsgType()
    {
        return msgType;
    }
    
    public void setMsgType(int msgType)
    {
        this.msgType = msgType;
    }
    
    public String getMsgContent()
    {
        return msgContent;
    }
    
    public void setMsgContent(String msgContent)
    {
        this.msgContent = msgContent;
    }
    
//    public MediaIndexModel getMediaIndex()
//    {
//        return mediaIndex;
//    }
//    
//    public void setMediaIndex(MediaIndexModel mediaIndex)
//    {
//        this.mediaIndex = mediaIndex;
//    }
    
    public int getMsgSendOrRecv()
    {
        return msgSendOrRecv;
    }
    
    public void setMsgSendOrRecv(int msgSendOrRecv)
    {
        this.msgSendOrRecv = msgSendOrRecv;
    }
    
    public int getMsgStatus()
    {
        return msgStatus;
    }
    
    public void setMsgStatus(int msgStatus)
    {
        this.msgStatus = msgStatus;
    }



	public String getMsgAttachmentSmall() {
		return msgAttachmentSmall;
	}



	public void setMsgAttachmentSmall(String msgAttachmentSmall) {
		this.msgAttachmentSmall = msgAttachmentSmall;
	}



	public int getMsgPlayTime() {
		return msgPlayTime;
	}



	public void setMsgPlayTime(int msgPlayTime) {
		this.msgPlayTime = msgPlayTime;
	}


    
    
//    public String getUserSysId()
//    {
//        return userSysId;
//    }
//    
//    public void setUserSysId(String userSysId)
//    {
//        this.userSysId = userSysId;
//    }
	
	//------------------------------------------------
	public String getSessionid() {
		return sessionid;
	}



	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}



	public String getGroupcreator() {
		return groupcreator;
	}
	public void setGroupcreator(String groupcreator) {
		this.groupcreator = groupcreator;
	}
	public String getGroupmember() {
		return groupmember;
	}
	public void setGroupmember(String groupmember) {
		this.groupmember = groupmember;
	}
	public String getGname() {
		return gname;
	}
	public void setGname(String gname) {
		this.gname = gname;
	}
	


	public int getGversion() {
		return gversion;
	}



	public void setGversion(int gversion) {
		this.gversion = gversion;
	}


	public String getFriendUserId()
    {
        return friendUserId;
    }

    public void setFriendUserId(String friendUserId)
    {
        this.friendUserId = friendUserId;
    }



	public String getMobile() {
		return mobile;
	}



	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
    
	public ContentValues toContentValues(){
		ContentValues result = new ContentValues();
		
		result.put("friendUserId", getFriendUserId());
		result.put("msgAttachment", getMsgAttachment());
		result.put("msgAttachmentSmall", getMsgAttachmentSmall());
		result.put("msgContent", getMsgContent());
		result.put("msgId", getMsgId());
		result.put("msgOwner", getMsgOwner());
		result.put("msgTime", getMsgTime());
		result.put("msgPlayTime", getMsgPlayTime());
		result.put("msgSendOrRecv", getMsgSendOrRecv());
		result.put("msgStatus", getMsgStatus());
		result.put("msgType", getMsgType());
		result.put("sessionid", getSessionid());
		result.put("gversion", getGversion());
		
		return result ;
	}
    
	//------------------------------------------------
    
}