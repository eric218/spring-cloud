package cn.wimetro.remotecall;

import com.unionpay.common.communication.onetoone.link.DuplexClientLink;
import com.unionpay.common.communication.onetoone.msgprotocol.IEventCallback;

public class ConnEventCallback implements IEventCallback {

    DuplexClientLink clientLink;


    public ConnEventCallback(DuplexClientLink clientLink){
        this.clientLink = clientLink;
    }
    //重写该方法可以用作链路的自动恢复
    @Override
    public void afterConnected() {
        ConnManager.getInstance().resume(clientLink);
    }
    //可以用作链路的自动隔离
    @Override
    public void afterDisconnected(){
        ConnManager.getInstance().insulate(clientLink);
    }
}
