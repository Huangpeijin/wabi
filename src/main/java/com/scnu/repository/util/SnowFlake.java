package com.scnu.repository.util;

import org.springframework.stereotype.Component;

/**
 *
 * 雪花算法：时间戳 数据中心 机器中心 序列号
 */
@Component
public class SnowFlake {
    /**
     * 起始的时间戳
     */
    private final static long START_STMP = 1480166465631L;

    private long workerId;    //工作id
    private long datacenterId;   //数据id
    //12位的序列号
    private long sequence;

    //长度为5位
    private long workerIdBits = 5L;
    private long datacenterIdBits = 5L;
    //最大值
    private long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    //序列号id长度
    private long sequenceBits = 12L;
    //序列号最大值
    private long sequenceMask = -1L ^ (-1L << sequenceBits);

    //工作id需要左移的位数，12位
    private long workerIdShift = sequenceBits;
    //数据id需要左移位数 12+5=17位
    private long datacenterIdShift = sequenceBits + workerIdBits;
    //时间戳需要左移位数 12+5+5=22位
    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;


    //上次时间戳，初始值为负数
    private long lastTimestamp = -1L;

    public long getWorkerId(){
        return workerId;
    }

    public long getDatacenterId(){
        return datacenterId;
    }

    public long getTimestamp(){
        return System.currentTimeMillis();
    }

    public SnowFlake() {
    }

    public SnowFlake(long workerId, long datacenterId, long sequence){
        // sanity check for workerId
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0",maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0",maxDatacenterId));
        }
        System.out.printf("worker starting. timestamp left shift %d, datacenter id bits %d, worker id bits %d, sequence bits %d, workerid %d",
                timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, workerId);

        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.sequence = sequence;
    }



    //下一个ID生成算法
    public synchronized long nextId() {
        long timestamp = timeGen();

        //获取当前时间戳如果小于上次时间戳，则表示时间戳获取出现异常
        if (timestamp < lastTimestamp) {
            System.err.printf("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
        }

        //获取当前时间戳如果等于上次时间戳（同一毫秒内），则在序列号加一；否则序列号赋值为0，从0开始。
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        //将上次时间戳值刷新
        lastTimestamp = timestamp;

        /**
         * 返回结果：
         * (timestamp - twepoch) << timestampLeftShift) 表示将时间戳减去初始时间戳，再左移相应位数
         * (datacenterId << datacenterIdShift) 表示将数据id左移相应位数
         * (workerId << workerIdShift) 表示将工作id左移相应位数
         * | 是按位或运算符，例如：x | y，只有当x，y都为0的时候结果才为0，其它情况结果都为1。
         * 因为个部分只有相应位上的值有意义，其它位上都是0，所以将各部分的值进行 | 运算就能得到最终拼接好的id
         */
        return ((timestamp - START_STMP) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    //获取时间戳，并与上次时间戳比较
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }


    //获取系统时间戳
    private long timeGen(){
        return System.currentTimeMillis();
    }
}

//package com.scnu.repository.util;
//
//import org.springframework.stereotype.Component;
//
//import java.text.ParseException;
//
///**
// * Twitter的分布式自增ID雪花算法
// **/
//@Component
//public class SnowFlake {
//
//    /**
//     * 起始的时间戳
//     */
//    private final static long START_STMP = 1609459200000L; // 2021-01-01 00:00:00
//
//    /**
//     * 每一部分占用的位数
//     */
//    private final static long SEQUENCE_BIT = 12; //序列号占用的位数，算法才用位运算，位运算是一个效率很高的一种运算
//    private final static long MACHINE_BIT = 5;   //机器标识占用的位数，占5位，一位就是二进制的零跟一，五位就是二的五次方就是三十二，所以最多可以表示三十二台机器
//    private final static long DATACENTER_BIT = 5;//数据中心占用的位数，比如上海可以叫一个数据中心....
//
//    /**
//     * 每一部分的最大值
//     */
//    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
//    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
//    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);
//
//    /**
//     * 每一部分向左的位移
//     */
//    private final static long MACHINE_LEFT = SEQUENCE_BIT;
//    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
//    private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;
//
//    private long datacenterId = 1;  //数据中心
//    private long machineId = 1;     //机器标识
//    private long sequence = 0L; //序列号
//    private long lastStmp = -1L;//上一次时间戳
//
//    public SnowFlake() {
//    }
//
//    public SnowFlake(long datacenterId, long machineId) {
//        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
//            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
//        }
//        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
//            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
//        }
//        this.datacenterId = datacenterId;
//        this.machineId = machineId;
//    }
//
//    /**
//     * 产生下一个ID
//     *
//     * @return
//     */
//    public synchronized long nextId() {
//        //取一个时间戳
//        long currStmp = getNewstmp();
//        if (currStmp < lastStmp) {
//            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
//        }
//
//        if (currStmp == lastStmp) {
//            //相同毫秒内，序列号自增
//            sequence = (sequence + 1) & MAX_SEQUENCE;
//            //同一毫秒的序列数已经达到最大
//            if (sequence == 0L) {
//                currStmp = getNextMill();
//            }
//        } else {
//            //不同毫秒内，序列号置为0
//            sequence = 0L;
//        }
//
//        lastStmp = currStmp;
//        //将数值拼起来，整个数值就变成了雪花id
//        return (currStmp - START_STMP) << TIMESTMP_LEFT //时间戳部分
//                | datacenterId << DATACENTER_LEFT       //数据中心部分
//                | machineId << MACHINE_LEFT             //机器标识部分
//                | sequence;                             //序列号部分
//    }
//
//    private long getNextMill() {
//        long mill = getNewstmp();
//        while (mill <= lastStmp) {
//            mill = getNewstmp();
//        }
//        return mill;
//    }
//
//    private long getNewstmp() {
//        return System.currentTimeMillis();
//    }
//
//    public static void main(String[] args) throws ParseException {
//       /* // 时间戳，以下两个是一样的，以毫秒为单位
//         System.out.println(System.currentTimeMillis());
//         System.out.println(new Date().getTime());
//        //以2021为起始时间，打印出来的是现在的时间跟起始时间的一个差值
//         String dateTime = "2021-01-01 08:00:00";
//         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//         System.out.println(sdf.parse(dateTime).getTime());*/
//
//        //创建一个雪花实例，数据中心和机器中心都是写成了1
//        SnowFlake snowFlake = new SnowFlake(1, 1);
//
//        long start = System.currentTimeMillis();
//        for (int i = 0; i < 10; i++) {
//            //通过nextId去用雪花，它由时间戳、数据中心、机器中心和序列号
//            System.out.println(snowFlake.nextId());
//            System.out.println(System.currentTimeMillis() - start);
//        }
//    }
//}
