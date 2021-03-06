package com.nbcb.mytomcat.catalina.session;

import org.apache.catalina.Manager;
import org.apache.catalina.Session;

import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public class FileStore extends StoreBase {

    /**
     * FileStore把session保存到这个目录下
     * 后续可以配置化
     */
    private String directory = "FILE_STORE";


    /**
     * session保存到文件系统后，使用的后缀
     */
    private static final String FILE_EXT = ".session";


    /**
     * 返回那些保存在文件系统中的session列表的session id
     * @return
     * @throws IOException
     */
    @Override
    public String[] keys() throws IOException {


        /**
         * FileStore目录
         */
        File fileStoreDir = directory();
        if(null == fileStoreDir || !fileStoreDir.exists()){
            return null;
        }


        /**
         * FileStore目录下所有的文件，这些文件是session对象的持久化信息
         * 文件名格式为：
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
     * 根据session id，确定这个session保存在持久化层的File对象
     * 一般来说，File path的格式为：
     * FILE_STORE/sid.session
     *
     * 比如：
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
     * 返回FileStore保存文件的目录
     * 比如： $project/FILE_STORE
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
     * 将内存中的session对象，持久化到文件系统
     * @param session
     * @throws IOException
     */
    @Override
    public void save(Session session) throws IOException {
        /**
         * 根据session id 创建一个File，这个文件就以sid命名
         */
        File file= file(session.getId());

        /**
         * 根据File创建OutputStream
         */
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try{
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(new BufferedOutputStream(fos));

            /**
             * 调用StandardSession.writeObject()方法
             * 将session对象写入到OutputStream
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
     * load的功能就是将session对象从持久化层中取出来
     * 取出来的逻辑参考save()
     * @param id
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public Session load(String id) {

        /**
         * 找到持久化层中对应session的文件
         */
        File file= file(id);

        /**
         * 文件不存在
         */
        if(!file.exists()){
            System.out.println("the loading session file does not exists!");
            return null;
        }

        /**
         * 如果session file存在，就调用Manager创建Session对象，
         * 并且将session file中反序列化出来的属性赋给这个新的session
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
     * 将那些保存在文件系统的(过期)session删除
     * @param id Session identifier of the Session to be removed
     *
     * @throws IOException
     */
    @Override
    public void remove(String id) throws IOException {
        /**
         * 先根据session id找出文件
         */
        File file = file(id);
        if(null == file || !file.exists()){
            return ;
        }

        /**
         * 删除这个文件
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
