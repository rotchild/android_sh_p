package cx.mobilechecksh.mvideo.camera.picture;

public class PictureParameters {
	
	public String title,directory,markName,time,userNo;
	public int resId;
	public double latitude,longtitude;
	public boolean	isAlpha;
	public int color;
	
	
	/**
	 * 员工编号
	 * @return
	 */
	public String getUserNo() {
		return userNo;
	}
	/**
	 * 员工编号
	 * @return
	 */
	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	/**
	 * 水印时间
	 * @return
	 */
	public String getTime() {
		return time;
	}
	/**
	 * 水印时间
	 * @return
	 */
	public void setTime(String time) {
		this.time = time;
	}
	public String getTitle() {
		return title;
	}
	/**
	 * 照片名
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDirectory() {
		return directory;
	}
	/**
	 * 照片路径
	 * @return
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	public String getMarkName() {
		return markName;
	}
	/**
	 * 水印名称
	 * @param markName
	 */
	public void setMarkName(String markName) {
		this.markName = markName;
	}
	public int getResId() {
		return resId;
	}
	/**
	 * 水印图标
	 * @param resId
	 */
	public void setResId(int resId) {
		this.resId = resId;
	}
	
	public double getLatitude() {
		return latitude;
	}
	/**
	 * 经度
	 * 
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongtitude() {
		return longtitude;
	}
	/**
	 * 维度
	 */
	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}
	/**
	 * 是否设置透明度
	 * @return
	 */
	public boolean isAlpha() {
		return isAlpha;
	}
	/**
	 * 是否设置透明度
	 * @param isAlpha
	 */
	public void setAlpha(boolean isAlpha) {
		this.isAlpha = isAlpha;
	}
	//获得水印颜色
	public int getColor() {
		return color;
	}
	/**
	 * 设置水印字体颜色
	 * @param color
	 */
	public void setColor(int color) {
		this.color = color;
	}
	
	
}
