package de.symeda.sormas.app.component.tagview;

import android.graphics.drawable.Drawable;

/**
 * Created by Orson on 03/01/2018.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class Tag {

    public int id;

    public String getText() {
        return text;
    }

    public String text;
    public int tagTextColor;
    public float tagTextSize;
    public int layoutColor;
    public int layoutColorPress;

    public boolean isDeletable;
    public int deleteIndicatorColor;
    public float deleteIndicatorSize;
    public float radius;
    public String deleteIcon;
    public float layoutBorderSize;
    public int layoutBorderColor;
    public Drawable background;

    private int rowLayout;



    public Tag(String text) {
        init(0, text, Constants.DEFAULT_TAG_TEXT_COLOR, Constants.DEFAULT_TAG_TEXT_SIZE, Constants.DEFAULT_TAG_LAYOUT_COLOR, Constants.DEFAULT_TAG_LAYOUT_COLOR_PRESS,
                Constants.DEFAULT_TAG_IS_DELETABLE, Constants.DEFAULT_TAG_DELETE_INDICATOR_COLOR, Constants.DEFAULT_TAG_DELETE_INDICATOR_SIZE, Constants.DEFAULT_TAG_RADIUS, Constants.DEFAULT_TAG_DELETE_ICON, Constants.DEFAULT_TAG_LAYOUT_BORDER_SIZE, Constants.DEFAULT_TAG_LAYOUT_BORDER_COLOR);
    }

    private void init(int id, String text, int tagTextColor, float tagTextSize,
                      int layoutColor, int layoutColorPress, boolean isDeletable,
                      int deleteIndicatorColor,float deleteIndicatorSize, float radius,
                      String deleteIcon, float layoutBorderSize, int layoutBorderColor) {
        this.id = id;
        this.text = text;
        this.tagTextColor = tagTextColor;
        this.tagTextSize = tagTextSize;
        this.layoutColor = layoutColor;
        this.layoutColorPress = layoutColorPress;
        this.isDeletable = isDeletable;
        this.deleteIndicatorColor = deleteIndicatorColor;
        this.deleteIndicatorSize = deleteIndicatorSize;
        this.radius = radius;
        this.deleteIcon = deleteIcon;
        this.layoutBorderSize = layoutBorderSize;
        this.layoutBorderColor = layoutBorderColor;
    }




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTagTextColor() {
        return tagTextColor;
    }

    public void setTagTextColor(int tagTextColor) {
        this.tagTextColor = tagTextColor;
    }

    public float getTagTextSize() {
        return tagTextSize;
    }

    public void setTagTextSize(float tagTextSize) {
        this.tagTextSize = tagTextSize;
    }

    public int getLayoutColor() {
        return layoutColor;
    }

    public void setLayoutColor(int layoutColor) {
        this.layoutColor = layoutColor;
    }

    public int getLayoutColorPress() {
        return layoutColorPress;
    }

    public void setLayoutColorPress(int layoutColorPress) {
        this.layoutColorPress = layoutColorPress;
    }

    public boolean isDeletable() {
        return isDeletable;
    }

    public void setDeletable(boolean deletable) {
        isDeletable = deletable;
    }

    public float getLayoutBorderSize() {
        return layoutBorderSize;
    }

    public void setLayoutBorderSize(float layoutBorderSize) {
        this.layoutBorderSize = layoutBorderSize;
    }

    public int getLayoutBorderColor() {
        return layoutBorderColor;
    }

    public void setLayoutBorderColor(int layoutBorderColor) {
        this.layoutBorderColor = layoutBorderColor;
    }

    public int getDeleteIndicatorColor() {
        return deleteIndicatorColor;
    }

    public void setDeleteIndicatorColor(int deleteIndicatorColor) {
        this.deleteIndicatorColor = deleteIndicatorColor;
    }

    public float getDeleteIndicatorSize() {
        return deleteIndicatorSize;
    }

    public void setDeleteIndicatorSize(float deleteIndicatorSize) {
        this.deleteIndicatorSize = deleteIndicatorSize;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public String getDeleteIcon() {
        return deleteIcon;
    }

    public void setDeleteIcon(String deleteIcon) {
        this.deleteIcon = deleteIcon;
    }

    public Drawable getBackground() {
        return background;
    }

    public void setBackground(Drawable background) {
        this.background = background;
    }

    public void setText(String text) {
        this.text = text;
    }



    public int getRowLayout() {
        return rowLayout;
    }

    public void setRowLayout(int rowLayout) {
        this.rowLayout = rowLayout;
    }
}
