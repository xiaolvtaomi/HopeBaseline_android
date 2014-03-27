package com.baseline.android.frame.logic;

import android.graphics.Bitmap;
import android.view.View;

//=================holder 内组件时间监听器定义===========================

public interface HolderEventListener {
	/**
     * 为某个view对象注册上下文菜单
     * <BR>
     * 
     * @param view View
     */
    void registerContextMenu(View view);
    
    /**
     * 某条消息的用户头像点击事件<BR>
     * @param msg BaseMessageModel
     */
    void onUserPhotoClick(BaseMessageModel msg);
    
    
    /**
     * 图片按钮点击事件<BR>
     * @param msg BaseMessageModel
     */
    void onImageClick(BaseMessageModel msg);
    
    /**
     * 视频播放按钮点击事件<BR>
     * @param msg BaseMessageModel
     */
    void onVideoClick(BaseMessageModel msg);
    
    /**
     * 获取指定路径的图片对象<BR>
     * @param path 图片本地路径
     * @return bitmap
     */
    Bitmap getBitmap(String path);
    
    /**
     * 
     * 获取贴图资源<BR>
     * @param path 贴图资源路径
     * @return 贴图
     */
    Bitmap getEmojBitmap(String path);
    
    /**
     * 
     * 开始播放音频<BR>
     * 
     * @param msg BaseMessageModel
     */
    void startPlayAudio(BaseMessageModel msg);
    
    /**
     * 停止播放音频
     * <BR>
     */
    void stopPlayAudio();
    
    /**
     * 
     * 更新消息状态为已读<BR>
     * 在显示消息的时候更新消息状态，如果消息未读，更新为已读；如果需要发送阅读报告，则发送阅读报告<BR>
     * @param msg BaseMessageModel
     */
    void setMsgAsReaded(BaseMessageModel msg);
    
    /**
     * 
     * 文本消息点击事件<BR>
     * @param msgModel BaseMessageModel
     */
    void onTextClick(BaseMessageModel msgModel);
    /**
     * 
     * 我的位置图片点击事件<BR>
     * @param msgModel BaseMessageModel
     */
    void onLocationImageClick(BaseMessageModel msgModel);
    /**
     * 
     * 下载地图缩略图<BR>
     * @param msgModel BaseMessageModel
     */
    void downLocationImage(BaseMessageModel msgModel);
    
    /**
     * 请求类型的同意按钮的点击事件<BR>
     * @param msgModel BaseMessageModel
     */
    void onImageRequestClick(BaseMessageModel msgModel);
}
