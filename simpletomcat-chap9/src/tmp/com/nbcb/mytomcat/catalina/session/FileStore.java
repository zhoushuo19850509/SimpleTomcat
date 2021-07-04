package com.nbcb.mytomcat.catalina.session;

import org.apache.catalina.Manager;
import org.apache.catalina.Session;

import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public class FileStore extends StoreBase {

    /**
     * FileStore��session���浽���Ŀ¼��
     * �����������û�
     */
    private String directory = "FILE_STORE";


    /**
     * session���浽�ļ�ϵͳ��ʹ�õĺ�׺
     */
    private static final String FILE_EXT = ".session";


    /**
     * ������Щ�������ļ�ϵͳ�е�session�б��session id
     * @return
     * @throws IOException
     */
    @Override
    public String[] keys() throws IOException {


        /**
         * FileStoreĿ¼
         */
        File fileStoreDir = directory();
        if(null == fileStoreDir || !fileStoreDir.exists()){
            return null;
        }


        /**
         * FileStoreĿ¼�����е��ļ�����Щ�ļ���session����ĳ־û���Ϣ
         * �ļ�����ʽΪ��
         * 28650efe4b4642c2bf7ec271b9c645ce.session
         */
        File[] files = fileStoreDir.listFiles();


        List<String> fileNames = new ArrayList<String>();
        String fileName = null;
        for(File file: files){
            fileName = file.getName();
            fileNames.add(fileName.substring(0,fileName.indexOf(FileStore.FILE_EXT)));
        }

        return (String[]) fileNames.toArray(new String[fileNames.size()]);
    }


    /**
     * ����session id��ȷ�����session�����ڳ־û����File����
     * һ����˵��File path�ĸ�ʽΪ��
     * FILE_STORE/sid.session
     *
     * ���磺
     * $project/FILE_STORE/209abddc8f8541daa3679c37b19d1130.session
     * @param sid
     * @return
     */
    private File file(String sid){

        if(null == directory){
            return null;
        }

        String fileName = sid + FILE_EXT;
        File file = new File(directory(), fileName);

        return file;
    }


    /**
     * ����FileStore�����ļ���Ŀ¼
     * ���磺 $project/FILE_STORE
     * @return
     */
    private File directory(){
        if(null == directory){
            return null;
        }

        File file = new File(directory);
        if(!file.exists()){
            file.mkdir();
        }
        return file;
    }


    /**
     * ���ڴ��е�session���󣬳־û����ļ�ϵͳ
     * @param session
     * @throws IOException
     */
    @Override
    public void save(Session session) throws IOException {
        /**
         * ����session id ����һ��File������ļ�����sid����
         */
        File file= file(session.getId());

        /**
         * ����File����OutputStream
         */
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try{
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(new BufferedOutputStream(fos));

            /**
             * ����StandardSession.writeObject()����
             * ��session����д�뵽OutputStream
             */
            ((StandardSession)session).writeObject(oos);

        }catch(Exception e){
            throw e;
        }finally {
            if(null != oos){
                oos.close();
            }

            if(null != fos){
                fos.close();
            }
        }
    }

    /**
     * load�Ĺ��ܾ��ǽ�session����ӳ־û�����ȡ����
     * ȡ�������߼��ο�save()
     * @param id
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public Session load(String id) {

        /**
         * �ҵ��־û����ж�Ӧsession���ļ�
         */
        File file= file(id);

        /**
         * �ļ�������
         */
        if(!file.exists()){
            System.out.println("the loading session file does not exists!");
            return null;
        }

        /**
         * ���session file���ڣ��͵���Manager����Session����
         * ���ҽ�session file�з����л����������Ը�������µ�session
         */
        Session session = (StandardSession)((ManagerBase)manager).createEmptySession();

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        ObjectInputStream ois = null;

        try{
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            ois = new ObjectInputStream(bis);

            ((StandardSession) session).readObject(ois);

        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }finally{
            try{
                if(null != fis){
                    fis.close();
                }
                if(null != bis){
                    bis.close();
                }
                if(null != ois){
                    ois.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }


        return session;
    }

    /**
     * ����Щ�������ļ�ϵͳ��(����)sessionɾ��
     * @param id Session identifier of the Session to be removed
     *
     * @throws IOException
     */
    @Override
    public void remove(String id) throws IOException {
        /**
         * �ȸ���session id�ҳ��ļ�
         */
        File file = file(id);
        if(null == file || !file.exists()){
            return ;
        }

        /**
         * ɾ������ļ�
         */
        file.delete();

    }

    public static void main(String[] args) throws IOException {


        System.out.println("start test FileStore.save()!");
        FileStore fs = new FileStore();
        Manager manager = new PersistentManager();
        ((PersistentManager) manager).setStore(fs);
        Session session = manager.createSession();
        fs.save(session);
        System.out.println("finish test FileStore.save()! sid: " + session.getId() );

        System.out.println("start test FileStore.load()!");
        String sid = session.getId();
        StandardSession loadedSession = (StandardSession)fs.load(sid);
        System.out.println(loadedSession.getCreationTime());
        System.out.println(loadedSession.getLastAccessedTime());
        System.out.println(loadedSession.getMaxInactiveInterval());
        System.out.println(loadedSession.isNew());
        System.out.println(loadedSession.isValid());
        System.out.println(loadedSession.getThisAccessTime());
        System.out.println("finish test FileStore.load()! sid: " + loadedSession.getId() );


        File dir = fs.directory();
        System.out.println("full path of file store directory: " + dir.getAbsolutePath() );

        String fileName = "28650efe4b4642c2bf7ec271b9c645ce.session";
        String mySid = fileName.substring(0,fileName.indexOf(FileStore.FILE_EXT));
        System.out.println("mySid: " + mySid);

        System.out.println("start print keys: >>>>>>>");
        String[] keys = fs.keys();
        for(String key: keys){
            System.out.println(key);
        }

        System.out.println("start remove from FILE_STORE");
        fs.remove("2529d06830584c1e81d3fb64bd55609d");
        System.out.println("finish remove from FILE_STORE");

    }

}
