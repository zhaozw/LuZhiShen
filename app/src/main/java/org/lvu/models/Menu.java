package org.lvu.models;

/**
 * Created by wuyr on 3/31/16 10:13 PM.
 */
public class Menu {

    private int mNameId;
    private int mImageId;

    public Menu(int imageId, int nameId) {
        mNameId = nameId;
        mImageId = imageId;
    }

    public int getNameId() {
        return mNameId;
    }

    public int getImageId() {
        return mImageId;
    }
}
