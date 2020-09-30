package cn.nkym.pt;

import cn.nkym.pt.notice.LmNotice;
import cn.nkym.pt.notice.MtNotice;
import cn.nkym.pt.sign.PTSignIn;
import cn.nkym.pt.sign.TBSignIn;

public class PTApplication {

    public static void main(String[] args) {
        multithreading();
    }

    static void multithreading() {
        /*new Thread(() -> {
            TBSignIn tbSignIn = new TBSignIn();
            tbSignIn.sign();
        }).start();
        new Thread(() -> {
            PTSignIn PTSignIn = new PTSignIn();
            PTSignIn.goSign();
        }).start();
        new Thread(() -> {
            MtNotice mtNotice = new MtNotice();
            mtNotice.mtnotice();
        }).start();*/
        new Thread(() -> {
            LmNotice lmNotice = new LmNotice();
            lmNotice.lmNotice();
        }).start();
    }
}
