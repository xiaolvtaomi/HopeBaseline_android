package com.baseline.android.frame.ui;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baseline.android.AppApplication;
import com.baseline.android.frame.logic.BaseLogicBuilder;
import com.baseline.android.frame.logic.ILogic;
import com.baseline.android.frame.logic.LogicBuilder;
import com.baseline.android.frame.utils.Logger;
import com.baseline.android.frame.view.dialog.FlippingLoadingDialog;
import com.hopebaseline.android.R;

public abstract class BaseActivity extends FragmentActivity {
	protected AppApplication mApplication;
	protected FlippingLoadingDialog mLoadingDialog;
	
	
	/**
	 * 屏幕的宽度、高度、密度
	 */
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected float mDensity;

	protected List<AsyncTask<Void, Void, Boolean>> mAsyncTasks = new ArrayList<AsyncTask<Void, Void, Boolean>>();
	
	private static final String TAG = "BaseActivity";
	/**
     * 系统的所有logic的缓存创建管理类
     */
    private static BaseLogicBuilder mLogicBuilder = null;
    /**
     * 该activity持有的handler类
     */
	private Handler mHandler = null;
	/**
     * 页面是否进入pause状态
     */
    private boolean isPaused;
    /**
     * 缓存持有的logic对象的集合
     */
    private final Set<ILogic> mLogicSet = new HashSet<ILogic>();
    /**
     * 是否独自控制logic监听
     */
    private boolean isPrivateHandler = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (!isInit())
        {
			this.mLogicBuilder = LogicBuilder.getInstance(this.getApplicationContext());
            Logger.i(TAG, "Load logic builder successful");
        }
		
		super.onCreate(savedInstanceState);
		mApplication = (AppApplication) getApplication();
		mLoadingDialog = new FlippingLoadingDialog(this, "请求提交中");

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		mDensity = metric.density;
		
		if (!isInit()){
            Logger.e(TAG,
                    "Launched the first should be the LauncheActivity's subclass:"
                            + this.getClass().getName());
            return;
        }
        
        if (!isPrivateHandler())
        {
            BaseActivity.mLogicBuilder.addHandlerToAllLogics(getHandler());
        }
        try
        {
            initLogics();
        }   
        catch (Exception e)
        {
            Toast.makeText(this.getApplicationContext(), "Init logics failed :"
                    + e.getMessage(), Toast.LENGTH_LONG);
            Logger.e(TAG, "Init logics failed :" + e.getMessage());
        }
	}
	
	@Override
    protected void onPause() {
        isPaused = true;
        super.onPause();
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		isPaused = false;
	}

	@Override
	protected void onDestroy() {
		clearAsyncTask();
		Handler handler = getHandler();
        if (handler != null)
        {
            if (mLogicSet.size() > 0 && isPrivateHandler())
            {
                for (ILogic logic : mLogicSet)
                {
                    logic.removeHandler(handler);
                }
            }
            else if (mLogicBuilder != null)
            {
                mLogicBuilder.removeHandlerToAllLogics(handler);
            }
            
        }
        
		super.onDestroy();
	}
	
	/**
     * 
     * 是否是paused状态<BR>
     * [功能详细描述]
     * 
     * @return boolean
     */
    protected boolean isPaused()
    {
        return isPaused;
    }
    
    /**
     * 初始化logic的方法，由子类实现<BR>
     * 在该方法里通过getLogicByInterfaceClass获取logic对象
     */
    protected void initLogics(){
    	
    };
	
	/**
     * 获取hander对象<BR>
     * @return 返回handler对象
     */
    protected Handler getHandler()
    {
        if (mHandler == null)
        {
            mHandler = new Handler()
            {
                public void handleMessage(Message msg)
                {
                    BaseActivity.this.handleStateMessage(msg);
                }
            };
        }
        return mHandler;
    }
    
    /**
     * activity是否已经初始化，加载了mLogicBuilder对象<BR>
     * 判断activiy中是否创建了mLogicBuilder对象
     * @return
     *      是否加载了mLogicBuilder
     */
    protected final boolean isInit()
    {
        return BaseActivity.mLogicBuilder != null;
    }
    
    
    /**
     * logic通过handler回调的方法<BR>
     * 通过子类重载可以实现各个logic的sendMessage到handler里的回调方法
     * @param msg
     *      Message对象
     */
    protected void handleStateMessage(Message msg)
    {
        
    }
    
    /**
     * 判断UI是否独自管理对logic的handler监听<BR>
     * @return
     *      是否是私有监听的handler
     */
    protected boolean isPrivateHandler()
    {
        return isPrivateHandler;
    }
    
    /**
     * 通过接口类获取logic对象<BR>
     * @param interfaceClass
     *      接口类型
     * @return
     *      logic对象
     */
    protected final ILogic getLogicByInterfaceClass(Class<?> interfaceClass)
    {
        ILogic logic = mLogicBuilder.getLogicByInterfaceClass(interfaceClass);
        if (isPrivateHandler() && null != logic && !mLogicSet.contains(logic))
        {
            logic.addHandler(getHandler());
            mLogicSet.add(logic);
        }
        if (logic == null)
        {
            Toast.makeText(this.getApplicationContext(),
                    "Not found logic by interface class (" + interfaceClass
                            + ")",
                    Toast.LENGTH_LONG);
            Logger.e(TAG, "Not found logic by interface class ("
                    + interfaceClass + ")");
            return null;
        }
        return logic;
    }
    
    /**
     * 设置全局的logic建造管理类<BR>
     * @param logicBuilder
     *      logic建造管理类
     */
    protected static final void setLogicBuilder(BaseLogicBuilder logicBuilder)
    {
        BaseActivity.mLogicBuilder = logicBuilder;
    }
    

	/** 初始化视图 **/
	protected abstract void initViews();

	/** 初始化事件 **/
	protected abstract void initEvents();

	protected void putAsyncTask(AsyncTask<Void, Void, Boolean> asyncTask) {
		mAsyncTasks.add(asyncTask.execute());
	}

	protected void clearAsyncTask() {
		Iterator<AsyncTask<Void, Void, Boolean>> iterator = mAsyncTasks
				.iterator();
		while (iterator.hasNext()) {
			AsyncTask<Void, Void, Boolean> asyncTask = iterator.next();
			if (asyncTask != null && !asyncTask.isCancelled()) {
				asyncTask.cancel(true);
			}
		}
		mAsyncTasks.clear();
	}

	protected void showLoadingDialog(String text) {
		if (!isPaused){
			if (text != null) {
				mLoadingDialog.setText(text);
			}
			mLoadingDialog.show();
		}
	}

	protected void dismissLoadingDialog() {
		if (mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
	}

	/** 短暂显示Toast提示(来自res) **/
	protected void showShortToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
	}

	/** 短暂显示Toast提示(来自String) **/
	protected void showShortToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	/** 长时间显示Toast提示(来自res) **/
	protected void showLongToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_LONG).show();
	}

	/** 长时间显示Toast提示(来自String) **/
	protected void showLongToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	/** 显示自定义Toast提示(来自res) **/
	protected void showCustomToast(int resId) {
		View toastRoot = LayoutInflater.from(BaseActivity.this).inflate(
				R.layout.common_toast, null);
		((TextView) toastRoot.findViewById(R.id.toast_text))
				.setText(getString(resId));
		Toast toast = new Toast(BaseActivity.this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	/** 显示自定义Toast提示(来自String) **/
	protected void showCustomToast(String text) {
		View toastRoot = LayoutInflater.from(BaseActivity.this).inflate(
				R.layout.common_toast, null);
		((TextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(BaseActivity.this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	

	/** 含有标题和内容的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message).show();
		return alertDialog;
	}

	/** 含有标题、内容、两个按钮的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message,
			String positiveText,
			DialogInterface.OnClickListener onPositiveClickListener,
			String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message)
				.setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener)
				.show();
		return alertDialog;
	}

	/** 含有标题、内容、图标、两个按钮的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message,
			int icon, String positiveText,
			DialogInterface.OnClickListener onPositiveClickListener,
			String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message).setIcon(icon)
				.setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener)
				.show();
		return alertDialog;
	}

	/** 默认退出 **/
	protected void defaultFinish() {
		super.finish();
	}
}
