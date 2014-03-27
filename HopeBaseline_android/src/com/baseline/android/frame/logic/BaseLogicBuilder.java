package com.baseline.android.frame.logic;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.os.Handler;

import com.baseline.android.frame.utils.Logger;

public abstract class BaseLogicBuilder implements ILogicBuilder
{
    private static final String TAG = "BaseLogicBuilder";
    
    /**
     * 对所有logic进行管理的缓�?
     */
    private Map<String, ILogic> mLogicCache = new HashMap<String, ILogic>();
    
    /**
     * 构�?方法，首先执行BaseLogicBuilder子类的init方法，然后对�?��logic进行初始化�?
     * @param context
     *      系统的context对象
     */
    protected BaseLogicBuilder(Context context)
    {
        init(context);
        initAllLogics(context);
    }
    
    /**
     * �?��被BaseLogicBuilder子类的init实现<BR>
     * BaseLogicBuilder子类�?��在该方法中�?过registerLogic进行注册logic�?
     * @param context
     *      系统的context对象
     */
    protected abstract void init(Context context);
    
    /**
     * 初始化所有的logic对象<BR>
     * 在该方法遍历缓存中所有的logic对象，执行所有logic的init方法
     * @param context
     *      系统的context对象
     */
    private void initAllLogics(Context context)
    {
        Set<Entry<String, ILogic>> logics = mLogicCache.entrySet();
        for (Entry<String, ILogic> logicEntry : logics)
        {
            logicEntry.getValue().init(context);
        }
    }
    
    /**
     * 根据接口类移除缓存中logic对象
     * 移除缓存中的logic对象<BR>
     * 根据接口�?可以将缓存中集中管理的logic对象移除
     * @param interfaceClass
     *      logic的接口类
     */
    public void removeLogic(Class<?> interfaceClass)
    {
        mLogicCache.remove(interfaceClass.getName());
    }
    
    /**
     * 根据接口类注册对应的logic对象到管理缓存中<BR>
     * �?��在子类的init方法中被执行，否则logic对象的init方法就不被调�?
     * @param interfaceClass
     *      logic的接口类
     * @param logic
     *      ILogic的实现类
     */
    protected void registerLogic(Class<?> interfaceClass, ILogic logic)
    {
        if (!mLogicCache.containsKey(interfaceClass.getName()))
        {
            String interfaceName = interfaceClass.getName();
            Class<?> logicClass = logic.getClass();
            if (isInterface(logicClass, interfaceName)
                    && isInterface(logicClass, ILogic.class.getName()))
            {
                mLogicCache.put(interfaceName, logic);
                Logger.i(TAG, "Register logic(" + logicClass.getName()
                        + ") successful.");
            }
            else
            {
                Logger.w(TAG, "Register logic(" + logicClass.getName()
                        + ") failed.");
            }
        }
    }
    
    /**
     * 对缓存中的所有logic对象增加hander<BR>
     * 对缓存中的所有logic对象增加hander，在该UI的onCreated时被框架执行�?
     * 如果该logic对象里执行了sendMessage方法，则�?��的活动的UI对象接收到�?知�?
     * @param handler
     *      UI的handler对象
     */
    public void addHandlerToAllLogics(Handler handler)
    {
        Set<Entry<String, ILogic>> logics = mLogicCache.entrySet();
        for (Entry<String, ILogic> logicEntry : logics)
        {
            ILogic logic = logicEntry.getValue();
            logic.addHandler(handler);
        }
    }
    
    /**
     * 对缓存中的所有logic对象移除hander对象<BR>
     * 在该UI的onDestory时被框架执行，如果该logic对象
     * 执行了sendMessage方法，则�?��的UI接收到�?�?
     * @param handler
     *      UI的handler对象
     */
    public void removeHandlerToAllLogics(Handler handler)
    {
        Set<Entry<String, ILogic>> logics = mLogicCache.entrySet();
        for (Entry<String, ILogic> logicEntry : logics)
        {
            ILogic logic = logicEntry.getValue();
            logic.removeHandler(handler);
        }
    }
    
    /**
     * 根据logic接口类返回logic对象<BR>
     * 如果缓存没有则返回null�?
     * @param interfaceClass
     *      logic接口�?
     * @return
     *      logic对象
     */
    public ILogic getLogicByInterfaceClass(Class<?> interfaceClass)
    {
        return mLogicCache.get(interfaceClass.getName());
    }
    
    /**
     * 判断�?��子类是否实现了接�?BR>
     * @param c 子类类型
     * @param szInterface 接口类名�?
     * @return
     *      是否实现了接�?
     */
    private boolean isInterface(Class<?> c, String szInterface)
    {
        Class<?>[] face = c.getInterfaces();
        for (int i = 0, j = face.length; i < j; i++)
        {
            if (face[i].getName().equals(szInterface))
            {
                return true;
            }
            else
            {
                Class<?>[] face1 = face[i].getInterfaces();
                for (int x = 0; x < face1.length; x++)
                {
                    if (face1[x].getName().equals(szInterface))
                    {
                        return true;
                    }
                    else if (isInterface(face1[x], szInterface))
                    {
                        return true;
                    }
                }
            }
        }
        if (null != c.getSuperclass())
        {
            return isInterface(c.getSuperclass(), szInterface);
        }
        return false;
    }
    
}
