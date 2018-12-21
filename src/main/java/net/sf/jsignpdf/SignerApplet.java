package net.sf.jsignpdf;

import javax.swing.*;
import java.io.File;

/**
 * Created by shital on 21-05-2015.
 */
public class SignerApplet extends JApplet {
    public void init() {
        System.out.println("init");
        System.out.println(System.getProperty("user.home"));
        super.init();
        sign();
    }

    public void sign() {
        System.out.println("sign");
//        String file = this.getParameter("filePath");
        BasicSignerOptions options = new BasicSignerOptions();
        options.setInFile("D:\\sample.pdf");
        options.setOutFile("D:\\signed.pdf");
        options.setKsType(Constants.KEYSTORE_TYPE_WINDOWS_MY);
        File file = new File("D:\\sample.pdf");
        System.out.println("exists =" + file.exists() + " can read= " + file.canRead() + " isfile=" + file.isFile());
        final SignerLogic signer = new SignerLogic(options);
        signer.signFile();
    }
}