package com.baseline.android.frame.logic;

import android.os.Handler;

public interface ILogicBuilder
{
    /**
     * 根据logic接口类返回logic对象<BR>
     * 如果缓存没有则返回null�?
     * @param interfaceClass
     *      logic接口�?
     * @return
     *      logic对象
     */
    public ILogic getLogicByInterfaceClass(Class<?> interfaceClass);

    /**
     * 对缓存中的所有logic对象增加hander<BR>
     * 对缓存中的所有logic对象增加hander，在该UI的onCreated时被框架执行�?
     * 如果该logic对象里执行了sendMessage方法，则�?��的活动的UI对象接收到�?知�?
     * @param handler
     *      UI的handler对象
     */
    public void addHandlerToAllLogics(Handler handler);

    /**
     * 对缓存中的所有logic对象移除hander对象<BR>
     * 在该UI的onDestory时被框架执行，如果该logic对象
     * 执行了sendMessage方法，则�?��的UI接收到�?�?
     * @param handler
     *      UI的handler对象
     */
    public void removeHandlerToAllLogics(Handler handler);
}
