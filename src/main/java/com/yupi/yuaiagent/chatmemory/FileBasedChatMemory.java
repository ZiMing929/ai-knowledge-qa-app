package com.yupi.yuaiagent.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 基于文件持久化的对话记忆
 */
public class FileBasedChatMemory implements ChatMemory {


    /**
     * 对话文件保存目录
     */
    private final String baseDir;


    /**
     * Kryo：
     * 用于 Java 对象的序列化和反序列化
     */
    private static final Kryo kryo = new Kryo();

    static {

        // 不要求所有类型提前注册
        kryo.setRegistrationRequired(false);

        // 设置对象实例化策略
        kryo.setInstantiatorStrategy(
                new StdInstantiatorStrategy()
        );
    }


    /**
     * 构造方法
     *
     * @param dir 文件保存目录
     */
    public FileBasedChatMemory(String dir) {

        this.baseDir = dir;

        File baseDirectory = new File(dir);

        // 目录不存在就创建
        if (!baseDirectory.exists()) {
            baseDirectory.mkdirs();
        }
    }


    /**
     * 向指定会话添加消息
     */
    @Override
    public void add(
            String conversationId,
            List<Message> messages) {

        // 先读取原来的聊天记录
        List<Message> conversationMessages =
                getOrCreateConversation(conversationId);

        // 添加本轮消息
        conversationMessages.addAll(messages);

        // 保存到文件
        saveConversation(
                conversationId,
                conversationMessages
        );
    }


    /**
     * 获取指定 conversationId 的全部聊天记忆
     *
     * 新版 Spring AI 的 ChatMemory
     * 要求实现 get(String conversationId)
     */
    @Override
    public List<Message> get(String conversationId) {         // spring ai 里面所有的消息数据都保存到 message 里面了！

        return getOrCreateConversation(conversationId);
    }


    /**
     * 清除指定会话
     */
    @Override
    public void clear(String conversationId) {

        File file =
                getConversationFile(conversationId);

        if (file.exists()) {

            boolean deleted = file.delete();

            if (!deleted) {
                System.err.println(
                        "删除对话文件失败: "
                                + file.getAbsolutePath()
                );
            }
        }
    }


    /**
     * 获取会话历史。
     *
     * 如果文件不存在：
     * 返回一个空 ArrayList。
     *
     * 如果文件存在：
     * 用 Kryo 从文件中反序列化出来。
     */
    @SuppressWarnings("unchecked")
    private List<Message> getOrCreateConversation(
            String conversationId) {

        File file =
                getConversationFile(conversationId);

        // 默认空消息列表           -----spring ai 里面所有的消息数据都保存到message里面了！
        List<Message> messages =
                new ArrayList<>();

        // 文件存在才读取
        if (file.exists()) {

            try (
                    Input input =
                            new Input(
                                    new FileInputStream(file)
                            )
            ) {

                messages =
                        kryo.readObject(
                                input,
                                ArrayList.class
                        );

            } catch (Exception e) {

                throw new RuntimeException(
                        "读取聊天记录失败，conversationId = "
                                + conversationId,
                        e
                );
            }
        }

        return messages;
    }


    /**
     * 保存聊天记录到文件
     */
    private void saveConversation(
            String conversationId,
            List<Message> messages) {      // spring ai 里面所有的消息数据都保存到message里面了！

        File file =
                getConversationFile(conversationId);

        try (
                Output output =
                        new Output(
                                new FileOutputStream(file)
                        )
        ) {

            kryo.writeObject(
                    output,
                    new ArrayList<>(messages)
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "保存聊天记录失败，conversationId = "
                            + conversationId,
                    e
            );
        }
    }


    /**
     * 根据 conversationId
     * 获取对应的 kryo 文件
     */
    private File getConversationFile(
            String conversationId) {

        return new File(
                baseDir,
                conversationId + ".kryo"
        );
    }
}