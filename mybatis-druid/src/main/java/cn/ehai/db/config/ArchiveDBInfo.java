package cn.ehai.db.config;

/**
 * 归档库数据信息
 *
 * @author lixiao
 * @date 2019-04-14 15:26
 */
public class ArchiveDBInfo {
    private String url;
    private String userName;
    private String passWord;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
