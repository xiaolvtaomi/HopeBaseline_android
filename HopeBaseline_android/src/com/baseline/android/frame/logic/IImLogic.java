package com.baseline.android.frame.logic;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;

import com.baseline.android.frame.utils.UriUtil.FromType;

/**
 * 聊天的逻辑处理接口定义<BR>
 */
public interface IImLogic {
	
	//============================
	/**
     * 
     * 注册1V1聊天的数据库监听<BR>
     * 1V1需要监听该好友对应的消息表以及该好友的用户信息表
     * @param friendUserID 聊天对象user id
     */
    void register1V1DataObserver(String friendUserID);
    
    /**
     * 
     * 注销1V1聊天的数据库监听<BR>
     * 与{@link #register1V1DataObserver(String)}对应
     * @param friendUserID 聊天对象user id
     */
    void unregister1V1DataObserver(String friendUserID);
    
    /**
     * 获取1V1聊天消息表的Cursor
     * <BR>
     * @param friendUserID 好友ID
     * @return  Cursor
     */
    BaseMsgCursorWrapper get1V1MsgList(String friendUserID);
    
    /**
     * 
     * 发送1V1消息（简单文本消息）<BR>
     * 
     * @param to 发往方
     * @param textContent 消息内容
     */
    void send1V1Message(String uid, String to,String mobile, String textContent);
    
    /**
     * 
     * 发送1V1消息（携带媒体附件）<BR>
     * 
     * @param to 发往方
     * @param textContent 消息内容
     * @param mediaIndex 媒体附件信息
     */
    void send1V1Message(String uid,String to, String textContent,
            MediaIndexModel mediaIndex);
    
    /**
     * 
     * 获取与指定好友的未读音频消息id列表<BR>
     * 
     * @param friendUserId 好友用户id
     * @return 未读音频消息id列表
     */
    List<String> get1V1UnreadAudioMsgIds(String friendUserId);
    
    /**
     * 获取头像
     * <BR>
     * 
     * @param userID 用户id
     * @return Drawable
     */
    Drawable getFace(String userID);
    
    /**
     * 获取自己的头像
     * <BR>
     * 
     * @return Drawable
     */
    Drawable getMyFace();
    
    /**
     * 获取自己的昵称
     * <BR>
     * 
     * @return String
     */
    String getMyNickName();
    
    /**
     * 
     * 进入1v1聊天页面后，把接收到的该好友的所有消息置为已读<BR>
     * @param friendUserId 好友id
     */
    void setAll1V1MsgAsReaded(String friendUserId);
    
    /**
     * 将1v1消息设为已读<BR>
     * @param msg MessageModel
     */
    void set1V1MsgAsReaded(BaseMessageModel msg);
    
    
    /**
     * * 1v1消息重发<BR>
     * @param msg MessageModel
     */
    void resend1V1Message(BaseMessageModel msg);
    
    /**
     * 
     * 清除1v1消息<BR>
     * @param friendUserID friendUserId
     */
    void clear1V1Message(String friendUserID);
    
    /**
     * 
     * 删除指定msdID的1V1聊天消息<BR>
     * 
     * @param msgId 消息id
     */
    void delete1V1Message(String msgId);
    
    /**
     * 
     * 转发一条1V1消息到多个好友<BR>
     * 该消息以1V1方式发送给多个好友
     * 
     * @param msgId 消息id
     * @param friendUserIds 要发往的用户id数组
     */
    void transfer1V1Message(String msgId, ArrayList<String> friendUserIds);
    
    
    /**
     * 
     * 获取音频保存路径<BR>
     * @return String 音频保存的路径
     */
    String getAudioFilePath();
    
    /**
     * 
     * 获取图片保存路径<BR>
     * @param fromType FromType 图片的来源类型
     * @return 图片保存路径
     */
    String getImageFilePath(FromType fromType);
  
    /**
     * 
     * 判断SD卡是否存在<BR>
     * @return boolean true存在
     */
    boolean sdCardExist();
    
}
