package com.gezitech.util;
/**
 * Controlls pagination
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class PagingUtil implements java.io.Serializable {
    private int page = -1;
    private int count = -1;
    private long sinceId = -1;
    private long maxId = -1;
    private static final long serialVersionUID = -3285857427993796670L;

    public PagingUtil() {
    }

    public PagingUtil(int page) {
        setPage(page);
    }

    public PagingUtil(long sinceId) {
        setSinceId(sinceId);
    }

    public PagingUtil(int page, int count) {
        this(page);
        setCount(count);
    }
    public PagingUtil(int page, long sinceId) {
        this(page);
        setSinceId(sinceId);
    }

    public PagingUtil(int page, int count, long sinceId) {
        this(page, count);
        setSinceId(sinceId);
    }

    public PagingUtil(int page, int count, long sinceId, long maxId) {
        this(page, count, sinceId);
        setMaxId(maxId);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        if (page < 1) {
            throw new IllegalArgumentException("page should be positive integer. passed:" + page);
        }
        this.page = page;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("count should be positive integer. passed:" + count);
        }
        this.count = count;
    }

    public PagingUtil count(int count) {
        setCount(count);
        return this;
    }

    public long getSinceId() {
        return sinceId;
    }

    public void setSinceId(int sinceId) {
        if (sinceId < 1) {
            throw new IllegalArgumentException("since_id should be positive integer. passed:" + sinceId);
        }
        this.sinceId = sinceId;
    }

    public PagingUtil sinceId(int sinceId) {
        setSinceId(sinceId);
        return this;
    }

    public void setSinceId(long sinceId) {
        if (sinceId < 1) {
            throw new IllegalArgumentException("since_id should be positive integer. passed:" + sinceId);
        }
        this.sinceId = sinceId;
    }

    public PagingUtil sinceId(long sinceId) {
        setSinceId(sinceId);
        return this;
    }

    public long getMaxId() {
        return maxId;
    }

    public void setMaxId(long maxId) {
        if (maxId < 1) {
            throw new IllegalArgumentException("max_id should be positive integer. passed:" + maxId);
        }
        this.maxId = maxId;
    }

    public PagingUtil maxId(long maxId) {
        setMaxId(maxId);
        return this;
    }
}