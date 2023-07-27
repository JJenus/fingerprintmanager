package com.jenus.bfpas.utils;

import com.jenus.bfpas.models.Finger;
import com.jenus.bfpas.models.Identifier;
import com.jenus.bfpas.models.Person;
import com.zkteco.biometric.FingerprintSensorErrorCode;
import com.zkteco.biometric.FingerprintSensorEx;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A utility class to interact with a ZK fingerprint sensor.
 */
public class FingerprintUtil {
    private int fpWidth = 0;
    private int fpHeight = 0;
    private byte[] lastRegTemp = new byte[2048];
    private int cbRegTemp = 0;
    private byte[][] regtemparray = new byte[3][2048];
    private int iFid = 1;
    private int nFakeFunOn = 1;
    static final int enroll_cnt = 3;
    private int enroll_idx = 0;
    private byte[] imgbuf = null;
    private byte[] template = new byte[2048];
    private int[] templateLen = new int[1];
    private boolean mbStop = true;
    private long mhDevice = 0;
    private long mhDB = 0;
    public StringProperty MESSAGE = new SimpleStringProperty("Start capture");
    private Image fingerPrintImage;
    private int statusCode = 0;

    Thread scannerThread;

    /**
     * Initialize the fingerprint sensor.
     *
     * @return True if the initialization is successful, otherwise false.
     */
    public boolean initialize() {
        MESSAGE.set("Loading...");
        // Initialize the fingerprint sensor
        if (FingerprintSensorErrorCode.ZKFP_ERR_OK != FingerprintSensorEx.Init()) {
            statusCode = 1;
            MESSAGE.set("Unable to initialize scanner");
            return false;
        }

        // Open the device
        int ret = FingerprintSensorEx.GetDeviceCount();
        if (ret < 0) {
            statusCode = 1;
            MESSAGE.set("Plug in a scanner");
            return false;
        }

        if (0 == (mhDevice = FingerprintSensorEx.OpenDevice(0))) {
            statusCode = 1;
            MESSAGE.set("Plug in properly");
            return false;
        }

        // Initialize the fingerprint database
        if (0 == (mhDB = FingerprintSensorEx.DBInit())) {
            statusCode = 1;
            MESSAGE.set("Unable to initialize database");
            return false;
        }

        // Set the fingerprint format to ISO or ANSI
        int nFmt = 0; // Default to ANSI
        FingerprintSensorEx.DBSetParameter(mhDB, 5010, nFmt);

        // Set other parameters if required
        //FingerprintSensorEx.SetParameter(mhDevice, 2002, changeByte(nFakeFunOn), 4);
        //Set DPI
        //int nDPI = 750;
        //FingerprintSensorEx.SetParameters(mhDevice, 3, changeByte(nDPI), 4);

        byte[] paramValue = new byte[4];
        int[] size = new int[1];
        //GetFakeOn
        //size[0] = 4;
        //FingerprintSensorEx.GetParameters(mhDevice, 2002, paramValue, size);
        //nFakeFunOn = byteArrayToInt(paramValue);

        size[0] = 4;
        FingerprintSensorEx.GetParameters(mhDevice, 1, paramValue, size);
        fpWidth = byteArrayToInt(paramValue);
        size[0] = 4;
        FingerprintSensorEx.GetParameters(mhDevice, 2, paramValue, size);
        fpHeight = byteArrayToInt(paramValue);
        //width = fingerprintSensor.getImageWidth();
        //height = fingerprintSensor.getImageHeight();
        imgbuf = new byte[fpWidth*fpHeight];

        // Start the worker thread for fingerprint capture and processing
        mbStop = false;

        return true;
    }

    /**
     * Free the resources used by the fingerprint sensor.
     */
    public void freeSensor() {
        mbStop = true;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (0 != mhDB) {
            FingerprintSensorEx.DBFree(mhDB);
            mhDB = 0;
        }

        if (0 != mhDevice) {
            FingerprintSensorEx.CloseDevice(mhDevice);
            mhDevice = 0;
        }

        FingerprintSensorEx.Terminate();
    }

    /**
     * Extract the fingerprint template from the provided image file path.
     *
     * @param imagePath The path of the fingerprint image.
     * @return The extracted fingerprint template as a byte array.
     * @throws IOException If an I/O error occurs during image extraction.
     */
    public byte[] extractTemplateFromImage(String imagePath) throws IOException {
        byte[] fpTemplate = new byte[2048];
        int[] sizeFPTemp = new int[1];
        sizeFPTemp[0] = 2048;

        int ret = FingerprintSensorEx.ExtractFromImage(mhDB, imagePath, 500, fpTemplate, sizeFPTemp);
        if (ret == 0) {
            return fpTemplate;
        } else {
            return null;
        }
    }

    /**
     * Enroll a fingerprint template.
     *
     */
    private void enrollTemplate() {
        // Perform the enrollment process
        // ... (Your enrollment logic here)
        int[] fid = new int[1];
        int[] score = new int [1];
        int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);
        if (ret == 0)
        {
            statusCode = 1;
            MESSAGE.set( "The finger already enroll by " + fid[0] + ",cancel enroll");
            enroll_idx = 0;
            return;
        }
        if (enroll_idx > 0 && FingerprintSensorEx.DBMatch(mhDB, regtemparray[enroll_idx-1], template) <= 0)
        {
            MESSAGE.set("Please press the same finger 3 times for the enrollment");
            statusCode = 3;
            return;
        }
        System.arraycopy(template, 0, regtemparray[enroll_idx], 0, 2048);
        enroll_idx++;
        if (enroll_idx == enroll_cnt) {
            int[] _retLen = new int[1];
            _retLen[0] = 2048;
            byte[] regTemp = new byte[_retLen[0]];

            if (0 == (ret = FingerprintSensorEx.DBMerge(mhDB, regtemparray[0], regtemparray[1], regtemparray[2], regTemp, _retLen)) &&
                    0 == (ret = FingerprintSensorEx.DBAdd(mhDB, iFid, regTemp))) {
                iFid++;
                cbRegTemp = _retLen[0];
                System.arraycopy(regTemp, 0, lastRegTemp, 0, cbRegTemp);
                String strBase64 = FingerprintSensorEx.BlobToBase64(regTemp, cbRegTemp);
                //Base64 Template
                statusCode = 0;
                MESSAGE.set("Enroll successful");
            } else {
                statusCode = 1;
                MESSAGE.set("enroll fail, error code=" + ret);
            }
        } else {
            MESSAGE.set("You need to press the " + (3 - enroll_idx) + " times fingerprint");
        }
    }

    public void startEnrollment(){
        scannerThread = new Thread(()->{
            if(!initialize()){
                return;
            }

            fingerPrintImage.progressProperty().addListener(((observableValue, oldValue, newVal) -> {
                if (newVal.doubleValue() >= 1){
                    enrollTemplate();
                    if (enroll_idx == enroll_cnt){
                        scannerThread.interrupt();
                    }
                }
            }));
        });

        scannerThread.start();
    }

    /**
     * Verify a fingerprint template.
     *
     */
    public boolean verifyTemplate(byte[] template) {
        // Perform the verification process
        // ... (Your verification logic here)
//        NEW CODE
        if (this.mhDB == 0 && !initialize()){
            System.out.println(MESSAGE);
        }

        int ret = FingerprintSensorEx.DBMatch(mhDB, lastRegTemp, template);
        if(ret > 0)
        {
            statusCode = 0;
            MESSAGE.set("Verify success, score=" + ret);
            return true;
        }
        else
        {
            statusCode = 1;
            MESSAGE.set("Verify fail, ret=" + ret);
            return false;
        }

    }

    /**
     * Identify fingerprint (I don't need it now)
     */
    public Identifier identify(List<Person> people){
        if (mhDB == 0 || !initialize()){
            return null;
        }

        AtomicReference<Identifier> idPerson = new AtomicReference<>();

        try{
            fingerPrintImage.progressProperty().addListener(((observableValue, oldValue, newValue) -> {
                if (oldValue.doubleValue() != newValue.doubleValue()){
                    idPerson.set(findPerson(people));
                }
            }));

            scannerThread = new Thread(this::startCapture);
            scannerThread.start();
        } catch (Exception e){
            e.printStackTrace();
        }

        return idPerson.get();
    }


    public Identifier findPerson(List<Person> people){
        for (Person person: people){
//            Get the person finger data and replace to new byte[2048] verify
            for(Finger finger: person.getRightHand().getFingers()){
                if (finger.getImageBytes() == null)
                    continue;
                boolean found = verifyTemplate(finger.getImageBytes());
                if (found){
                    return new Identifier(person, finger.getName());
                }
            }
        }

        return null;
    }

    /**
     * Convert an integer to a byte array.
     *
     * @param number The integer to convert.
     * @return The byte array representation of the integer.
     */
    public static byte[] intToByteArray(final int number) {
        // ... Code to convert int to byte array ...
        byte[] abyte = new byte[4];
        // "&" 与（AND），对两个整型操作数中对应位执行布尔代数，两个位都为1时输出1，否则0。
//    (EN)    "&" is the bitwise AND operator, which performs Boolean algebra on
//    corresponding bits of two integer operands. It outputs 1 only when
//    both bits are 1; otherwise, it outputs 0.

        abyte[0] = (byte) (0xff & number);
        // ">>"右移位，若为正数则高位补0，若为负数则高位补1
        abyte[1] = (byte) ((0xff00 & number) >> 8);
        abyte[2] = (byte) ((0xff0000 & number) >> 16);
        abyte[3] = (byte) ((0xff000000 & number) >> 24);
        return abyte;
    }

    /**
     * Convert a byte array to an integer.
     *
     * @param bytes The byte array to convert.
     * @return The integer representation of the byte array.
     */
    public static int byteArrayToInt(byte[] bytes) {
        int number = bytes[0] & 0xFF;
        // "|="按位或赋值。
        number |= ((bytes[1] << 8) & 0xFF00);
        number |= ((bytes[2] << 16) & 0xFF0000);
        number |= ((bytes[3] << 24) & 0xFF000000);
        return number;
    }

    /**
     * Main method to show an example of how to use the FingerprintUtil class.
     * //        export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/lib/zkteco/lib-x64
     */
    private void OnCatpureOK(byte[] imgBuf)
    {
        try {
            writeBitmap(imgBuf, fpWidth, fpHeight, "fingerprint.bmp");

            fingerPrintImage = new Image("fingerprint.bmp");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void writeBitmap(byte[] imageBuf, int nWidth, int nHeight,
                                  String path) throws IOException {
        java.io.FileOutputStream fos = new java.io.FileOutputStream(path);
        java.io.DataOutputStream dos = new java.io.DataOutputStream(fos);

        int w = (((nWidth+3)/4)*4);
        int bfType = 0x424d; // 位图文件类型（0—1字节）
        int bfSize = 54 + 1024 + w * nHeight;// bmp文件的大小（2—5字节）
        int bfReserved1 = 0;// 位图文件保留字，必须为0（6-7字节）
        int bfReserved2 = 0;// 位图文件保留字，必须为0（8-9字节）
        int bfOffBits = 54 + 1024;// 文件头开始到位图实际数据之间的字节的偏移量（10-13字节）

        dos.writeShort(bfType); // 输入位图文件类型'BM'
        dos.write(changeByte(bfSize), 0, 4); // 输入位图文件大小
        dos.write(changeByte(bfReserved1), 0, 2);// 输入位图文件保留字
        dos.write(changeByte(bfReserved2), 0, 2);// 输入位图文件保留字
        dos.write(changeByte(bfOffBits), 0, 4);// 输入位图文件偏移量

        int biSize = 40;// 信息头所需的字节数（14-17字节）
        int biWidth = nWidth;// 位图的宽（18-21字节）
        int biHeight = nHeight;// 位图的高（22-25字节）
        int biPlanes = 1; // 目标设备的级别，必须是1（26-27字节）
        int biBitcount = 8;// 每个像素所需的位数（28-29字节），必须是1位（双色）、4位（16色）、8位（256色）或者24位（真彩色）之一。
        int biCompression = 0;// 位图压缩类型，必须是0（不压缩）（30-33字节）、1（BI_RLEB压缩类型）或2（BI_RLE4压缩类型）之一。
        int biSizeImage = w * nHeight;// 实际位图图像的大小，即整个实际绘制的图像大小（34-37字节）
        int biXPelsPerMeter = 0;// 位图水平分辨率，每米像素数（38-41字节）这个数是系统默认值
        int biYPelsPerMeter = 0;// 位图垂直分辨率，每米像素数（42-45字节）这个数是系统默认值
        int biClrUsed = 0;// 位图实际使用的颜色表中的颜色数（46-49字节），如果为0的话，说明全部使用了
        int biClrImportant = 0;// 位图显示过程中重要的颜色数(50-53字节)，如果为0的话，说明全部重要

        dos.write(changeByte(biSize), 0, 4);// 输入信息头数据的总字节数
        dos.write(changeByte(biWidth), 0, 4);// 输入位图的宽
        dos.write(changeByte(biHeight), 0, 4);// 输入位图的高
        dos.write(changeByte(biPlanes), 0, 2);// 输入位图的目标设备级别
        dos.write(changeByte(biBitcount), 0, 2);// 输入每个像素占据的字节数
        dos.write(changeByte(biCompression), 0, 4);// 输入位图的压缩类型
        dos.write(changeByte(biSizeImage), 0, 4);// 输入位图的实际大小
        dos.write(changeByte(biXPelsPerMeter), 0, 4);// 输入位图的水平分辨率
        dos.write(changeByte(biYPelsPerMeter), 0, 4);// 输入位图的垂直分辨率
        dos.write(changeByte(biClrUsed), 0, 4);// 输入位图使用的总颜色数
        dos.write(changeByte(biClrImportant), 0, 4);// 输入位图使用过程中重要的颜色数

        for (int i = 0; i < 256; i++) {
            dos.writeByte(i);
            dos.writeByte(i);
            dos.writeByte(i);
            dos.writeByte(0);
        }

        byte[] filter = null;
        if (w > nWidth)
        {
            filter = new byte[w-nWidth];
        }

        for(int i=0;i<nHeight;i++)
        {
            dos.write(imageBuf, (nHeight-1-i)*nWidth, nWidth);
            if (w > nWidth)
                dos.write(filter, 0, w-nWidth);
        }
        dos.flush();
        dos.close();
        fos.close();
    }

    public static byte[] changeByte(int data) {
        return intToByteArray(data);
    }

    private void startCapture(){
        int ret;

        while (!mbStop) {
            templateLen[0] = 2048;
            if (0 == FingerprintSensorEx.AcquireFingerprint(mhDevice, imgbuf, template, templateLen))
            {
                if (nFakeFunOn == 1)
                {
                    byte[] paramValue = new byte[4];
                    int[] size = new int[1];
                    size[0] = 4;
                    int nFakeStatus = 0;
                    //GetFakeStatus
                    ret = FingerprintSensorEx.GetParameters(mhDevice, 2004, paramValue, size);
                    nFakeStatus = byteArrayToInt(paramValue);
                    System.out.println("ret = "+ ret +",nFakeStatus=" + nFakeStatus);
                    if (0 == ret && (byte)(nFakeStatus & 31) != 31)
                    {
                        MESSAGE.set("Is a fake-finer?");
                        return;
                    }
                }
                OnCatpureOK(imgbuf);
//                    OnExtractOK(template, templateLen[0]);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public byte[] getFingerTemplate() {
        return lastRegTemp;
    }

    public Image getImage() {
        return fingerPrintImage;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
