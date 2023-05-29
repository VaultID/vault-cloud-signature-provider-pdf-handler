package net.sf.jsignpdf;

/**
 *
 */
public class SecundarySignerOptions {
    
    private boolean visible;
    private String visibileSignatureField = "";
    private int page = Constants.DEFVAL_PAGE;
    private float positionLLX = Constants.DEFVAL_LLX;
    private float positionLLY = Constants.DEFVAL_LLY;
    private float positionURX = Constants.DEFVAL_URX;
    private float positionURY = Constants.DEFVAL_URY;
    private float bgImgScale = Constants.DEFVAL_BG_SCALE;
    private float l2TextFontSize = Constants.DEFVAL_L2_FONT_SIZE;
    private String imgPath;
    private String bgImgPath;
    private boolean acro6Layers = Constants.DEFVAL_ACRO6LAYERS;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getVisibileSignatureField() {
        return visibileSignatureField;
    }

    public void setVisibileSignatureField(String visibileSignatureField) {
        this.visibileSignatureField = visibileSignatureField;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public float getPositionLLX() {
        return positionLLX;
    }

    public void setPositionLLX(float positionLLX) {
        this.positionLLX = positionLLX;
    }

    public float getPositionLLY() {
        return positionLLY;
    }

    public void setPositionLLY(float positionLLY) {
        this.positionLLY = positionLLY;
    }

    public float getPositionURX() {
        return positionURX;
    }

    public void setPositionURX(float positionURX) {
        this.positionURX = positionURX;
    }

    public float getPositionURY() {
        return positionURY;
    }

    public void setPositionURY(float positionURY) {
        this.positionURY = positionURY;
    }

    public float getBgImgScale() {
        return bgImgScale;
    }

    public void setBgImgScale(float bgImgScale) {
        this.bgImgScale = bgImgScale;
    }

    public float getL2TextFontSize() {
        return l2TextFontSize;
    }

    public void setL2TextFontSize(float l2TextFontSize) {
        this.l2TextFontSize = l2TextFontSize;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getBgImgPath() {
        return bgImgPath;
    }

    public void setBgImgPath(String bgImgPath) {
        this.bgImgPath = bgImgPath;
    }

    public boolean isAcro6Layers() {
        return acro6Layers;
    }

    public void setAcro6Layers(boolean acro6Layers) {
        this.acro6Layers = acro6Layers;
    }
}
