package cn.wimetro.remotecall;

import com.unionpay.common.communication.concurrentutil.asyntosyn.WaitResultSet;
import com.unionpay.common.communication.onetoone.link.DuplexClientLink;
import com.unionpay.common.communication.onetoone.linkstate.StateWrongException;
import com.unionpay.common.communication.onetoone.platform.CommuPlatform;
import com.unionpay.common.communication.onetoone.platform.SimpleThreadStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ConnManager {
    //日志
    //private static Logger log = LoggerFactory.getLogger(ConnManager.class);

//    private String nodeIp ="";
//    private int nodePort =123;
    List<YlNode> nodeList = new ArrayList<YlNode>();



    private static volatile ConnManager singleton;

    WaitResultSet<String, MessageVO> waitSet = new WaitResultSet<String, MessageVO>();

    public WaitResultSet<String, MessageVO> getWaitSet() {
        return waitSet;
    }

    public void setWaitSet(WaitResultSet<String, MessageVO> waitSet) {
        this.waitSet = waitSet;
    }

    /** 所有主机链路列表 */
    private List<DuplexClientLink> allLinkList = new ArrayList<DuplexClientLink>();
    /** 可用主机链表列表 */
    private List<DuplexClientLink> availableLinkList = new ArrayList<DuplexClientLink>();
    /** 不可用主机链表列表 */
    private List<DuplexClientLink> unAvailableLinkList = new ArrayList<DuplexClientLink>();
    //TODO 初始化链接，启动连接
    public void start(String localIp,List<YlNode> nodeList){
        try {
            if (!CommuPlatform.getInstance().isStarted()) {
                CommuPlatform.getInstance().setThreadStrategy(new SimpleThreadStrategy());
                CommuPlatform.getInstance().start();
            }


            for (YlNode napNode : nodeList) {

                DuplexClientLink clientLink = new DuplexClientLink(localIp, napNode.getPort(), napNode.getIp(),
                        new ConnMsgProtocol());
                clientLink.setMsgProcessor(new ConnMsgProcessor());
//                if (masterNap == null) {
//                    masterNap = napNode;
//                }


                clientLink.setEventCallback( new ConnEventCallback(clientLink));

                clientLink.start();
                allLinkList.add(clientLink);
            }

           log.info("银联远程服务已启动");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public void stop (){
        try {
            for (DuplexClientLink clientLink : allLinkList) {
                clientLink.stop();
            }
        } catch (Exception e) {
        }
    }
    public void sendMsg(MessageVO msg){
        try {
            DuplexClientLink clientLink = loadBalance();
            clientLink.sendMsg(msg);
        } catch (StateWrongException e) {
        }
    }
    /**
     * 异常链路隔离，将链路对象从可用集合迁移到不可用集合
     *
     * @param clientLink
     */
    public synchronized void insulate(DuplexClientLink clientLink) {
        if (!allLinkList.contains(clientLink)) {
            throw new IllegalArgumentException("该链路不在" + "连接管理集合范围内容!");
        }

        if (availableLinkList.contains(clientLink)) {
            availableLinkList.remove(clientLink);
        }

        if (!unAvailableLinkList.contains(clientLink)) {
            unAvailableLinkList.add(clientLink);
        }
    }

    /**
     * 链路恢复，将链路集合从不可用链路集合迁移回可用链路集合
     *
     * @param clientLink
     */
    public synchronized void resume(DuplexClientLink clientLink) {
        if (!allLinkList.contains(clientLink)) {
            throw new IllegalArgumentException("该链路不在"  + "连接管理集合范围内容!");
        }

        if (unAvailableLinkList.contains(clientLink)) {
            unAvailableLinkList.remove(clientLink);
        }

        if (!availableLinkList.contains(clientLink)) {
//            if (clientLink.getRemoteIp().equals(masterNap.getIp()) && clientLink.getRemotePort() == masterNap.getPort()) {
//                availableLinkList.add( clientLink);
//            } else {
//                availableLinkList.add(clientLink);
//            }

            availableLinkList.add( clientLink);
        }
    }




    /**
     *
     * @return
     */
    private DuplexClientLink loadBalance() {
        if (availableLinkList.size() == 0) {
            throw new RuntimeException( "无可用连接！");
        }

        DuplexClientLink clientLink = availableLinkList.get(0);
        return clientLink;
    }


    private ConnManager() {}

    public static ConnManager getInstance() {
        if (singleton == null) {
            synchronized (ConnManager.class) {
                if (singleton == null) {
                    singleton = new ConnManager();
                }
            }
        }
        return singleton;
    }



}
