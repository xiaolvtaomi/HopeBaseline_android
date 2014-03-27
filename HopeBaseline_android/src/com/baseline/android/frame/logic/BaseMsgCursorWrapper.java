package com.baseline.android.frame.logic;

import android.database.Cursor;
import android.database.CursorWrapper;


public abstract class BaseMsgCursorWrapper extends CursorWrapper {

	/**
     * count
     */
    private int mCount;
	
	public BaseMsgCursorWrapper(Cursor cursor) {
		super(cursor);
	}

	
	@Override
    public boolean moveToPosition(int position)
    {
        return super.moveToPosition((super.getCount() - mCount) + position);
    }
	
	@Override
    public int getCount()
    {
        return mCount;
    }
	public int setCount(int count)
    {
        this.mCount = super.getCount() > count ? count : super.getCount();
        return this.mCount;
    }
	/**
     * 
     * 判断此Cursor持有的记录数是否与父类相同<BR>
     * 
     * @return 是否同父类
     */
    public boolean sameAsSuper()
    {
        return this.mCount == super.getCount();
    }
    //TODO:从cursor中解析出BaseMessageModel对象,可以写两个MsgCursorWrapper的子类，
    // 分别实现1V1的消息解析和1VN的消息解析
    public abstract BaseMessageModel parseMsgModel();

}
