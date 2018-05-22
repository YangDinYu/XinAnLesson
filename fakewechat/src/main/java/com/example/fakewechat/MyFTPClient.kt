package com.example.fakewechat

import android.util.Log
import org.apache.commons.net.ftp.FTPClient

import java.net.URL
import org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE
import java.io.*
import org.apache.commons.net.ftp.FTPFile
import android.R.attr.port
import org.apache.commons.net.ftp.FTPReply
import org.apache.commons.net.imap.IMAPReply.getReplyCode
import android.content.ContentResolver








/**
 * Created by ydy on 18-5-14.
 */
object MyFTPClient {

    public lateinit var ftpClient:FTPClient;


    init {
        ftpClient = FTPClient();
        ftpClient.connect("47.90.123.155", 21) // IP地址和端口
        ftpClient.login("uftp", "hs4cdpd854") // 用户名和密码，匿名登陆的话用户名为anonymous,密码为非空字符串
        ftpClient.enterLocalPassiveMode();
    }

    fun ReConnect(){
        ftpClient = FTPClient();
        ftpClient.connect("47.90.123.155", 21) // IP地址和端口
        ftpClient.login("uftp", "hs4cdpd854") // 用户名和密码，匿名登陆的话用户名为anonymous,密码为非空字符串
        ftpClient.enterLocalPassiveMode();
    }

 /*   fun writeFileToFtp(fileName: String, content: String) {
        try {
            //fileName为文件的名称，带后缀。例如：ftpInfo.txt
            Log.i("dir", ftpClient.printWorkingDirectory())
*//*            val hasDir = ftpClient.changeWorkingDirectory("/home/uftp") // 切换到test目录 ,返回boolean值，如果有该文件夹返回true，否则，返回false
            if (!hasDir) {
                //创建文件夹
                ftpClient.makeDirectory("userBehavior")
                ftpClient.changeWorkingDirectory("userBehavior")
            }else{
                Log.i("test","没有这个目录")
            }*//*




            ReConnect()
            ftpClient.setControlEncoding("utf-8")
            //向指定文件写入内容，如果没有该文件，则先创建文件再写入。写入的方式是追加。
            var stream = ftpClient.appendFileStream(fileName);
            stream.write("tesssst".toByteArray())

            var pw = PrintWriter(OutputStreamWriter(ftpClient.appendFileStream(fileName),"utf-8"),true);
            ftpClient.appendFileStream(fileName).write("test".toByteArray())
            pw.write(content)
            pw.flush()
            pw.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }*/

    /**
     * 上传文件
     * @param pathname ftp服务保存地址
     * @param fileName 上传到ftp的文件名
     *  @param originfilename 待上传文件的名称（绝对地址） *
     * @return
     */
    fun uploadFile(pathname: String, fileName: String, originfilename: String): Boolean {
        var flag = false
        var inputStream: InputStream? = null
        try {

            println("开始上传文件")
            inputStream = FileInputStream(File(originfilename))

            ReConnect();
            val replyCode = ftpClient.replyCode //是否成功登录服务器
            System.out.println("replycode:"+replyCode);
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("connect failed...ftp服务器:" )
            }else {
                System.out.println("connect successfu...ftp服务器:")
            }


            ftpClient.deleteFile(pathname + fileName);

            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE)
            CreateDirecroty(pathname)
            ftpClient.makeDirectory(pathname)
            ftpClient.changeWorkingDirectory(pathname)
            ftpClient.storeFile(fileName, inputStream)
            inputStream!!.close()
            ftpClient.logout()
            flag = true
            println("上传文件成功")
        } catch (e: Exception) {
            println("上传文件失败")
            e.printStackTrace()
        } finally {
            if (ftpClient.isConnected) {
                try {
                    ftpClient.disconnect()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            if (null != inputStream) {
                try {
                    inputStream!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return true
    }

    @Throws(IOException::class)
    fun CreateDirecroty(remote: String): Boolean {
        val success = true
        val directory = "$remote/"
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equals("/", ignoreCase = true) && !changeWorkingDirectory(directory)) {
            var start = 0
            var end = 0
            if (directory.startsWith("/")) {
                start = 1
            } else {
                start = 0
            }
            end = directory.indexOf("/", start)
            var path = ""
            var paths = ""
            while (true) {
                val subDirectory = String(remote.substring(start, end).toByteArray(charset("GBK")))
                path = "$path/$subDirectory"
                if (!existFile(path)) {
                    if (makeDirectory(subDirectory)) {
                        changeWorkingDirectory(subDirectory)
                    } else {
                        println("创建目录[$subDirectory]失败")
                        changeWorkingDirectory(subDirectory)
                    }
                } else {
                    changeWorkingDirectory(subDirectory)
                }

                paths = "$paths/$subDirectory"
                start = end + 1
                end = directory.indexOf("/", start)
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break
                }
            }
        }
        return success
    }

    fun changeWorkingDirectory(directory: String): Boolean {
        var flag = true
        try {
            flag = ftpClient.changeWorkingDirectory(directory)
            if (flag) {
                println("进入文件夹$directory 成功！")

            } else {
                println("进入文件夹$directory 失败！开始创建文件夹")
            }
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }

        return flag
    }

    //判断ftp服务器文件是否存在
    @Throws(IOException::class)
    fun existFile(path: String): Boolean {
        var flag = false
        val ftpFileArr = ftpClient.listFiles(path)
        if (ftpFileArr.size > 0) {
            flag = true
        }
        return flag
    }

    //创建目录
    fun makeDirectory(dir: String): Boolean {
        var flag = true
        try {
            flag = ftpClient.makeDirectory(dir)
            if (flag) {
                println("创建文件夹$dir 成功！")

            } else {
                println("创建文件夹$dir 失败！")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return flag
    }

    fun downloadFile(pathname: String, filename: String, localpath: String): Boolean {
        var flag = false
        var os: OutputStream? = null
        try {
            println("开始下载文件")
            ReConnect()
            //切换FTP目录
            ftpClient.changeWorkingDirectory(pathname)
            val ftpFiles = ftpClient.listFiles()
            for (file in ftpFiles) {
                if (filename.equals(file.name, ignoreCase = true)) {
                    val localFile = File(localpath + "/" + file.name)
                    os = FileOutputStream(localFile)
                    ftpClient.retrieveFile(file.name, os)
                    os.close()
                }
            }
            ftpClient.logout()
            flag = true
            println("下载文件成功")

        } catch (e: Exception) {
            println("下载文件失败")
            e.printStackTrace()
        } finally {
            if (ftpClient.isConnected) {
                try {
                    ftpClient.disconnect()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            if (null != os) {
                try {
                    os.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return flag
    }
}