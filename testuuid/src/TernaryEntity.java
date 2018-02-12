import java.io.Serializable;

/**
 * @Author: xxxindy
 * @Date:2018/1/29 下午1:48
 * @Description:
 */
public class TernaryEntity<K,V,R> implements Serializable {
    private K key;
    private V value;
    private R remark;
    /**
     * getter method
     * @return the key
     */
    public K getKey() {
        return key;
    }
    /**
     * setter method
     * @param key the key to set
     */
    public void setKey(K key) {
        this.key = key;
    }
    /**
     * getter method
     * @return the value
     */
    public V getValue() {
        return value;
    }
    /**
     * setter method
     * @param value the value to set
     */
    public void setValue(V value) {
        this.value = value;
    }
    /**
     * getter method
     * @return the remark
     */
    public R getRemark() {
        return remark;
    }
    /**
     * setter method
     * @param remark the remark to set
     */
    public void setRemark(R remark) {
        this.remark = remark;
    }

    /**
     * 构建器，不建议使用直接的 new TernaryEntity<K, V, R>();
     * @return
     */
    public static<K, V, R> TernaryEntity<K, V, R> create(){
        return new TernaryEntity<K, V, R>();
    }
}