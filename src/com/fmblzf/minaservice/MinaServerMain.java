package com.fmblzf.minaservice;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * 
 * @Copy：2017-fmblzf
 * @ProjectName：MINA_SERVER
 * 
 * @ClassDecription：
 * @ClassName：com.fmblzf.minaservice.MinaServerMain
 * @Creator：zhaofeng
 * @CreatTime：2017年5月27日 上午11:50:55
 * @FixPerson：fmblzf
 * @FixTime：2017年5月27日 上午11:50:55
 * @Tag：
 * @version V1.0
 *
 */
public class MinaServerMain {
	// 设置端口号
	private static final int PORT = 9321;
	
//	private ConcurrentHashMap<> clients;

	public static void main(String[] args) throws IOException {
		createUdp();
		createTcp();
	}

	/**
	 * 创建UDP连接
	 * @Title: createUdp 
	 * @Description: TODO 
	 * @throws IOException 
	 *
	 */
	private static void createUdp() throws IOException {
		IoAcceptor ioAcceptor = new NioDatagramAcceptor();
		// 添加过滤器
		ioAcceptor.getFilterChain().addLast("logger", new LoggingFilter());// 设置日志管理过滤器
		ioAcceptor.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));// 设置字节处理过滤器
		// 添加IOHandler
		ioAcceptor.setHandler(new MainServerHandler());
		// 添加Session配置
		ioAcceptor.getSessionConfig().setReadBufferSize(2048);// 设置读缓存区的大小
		ioAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);// 设置读写的空闲时间都是10秒
		ioAcceptor.bind(new InetSocketAddress(PORT+1));
	}
	/**
	 * 创建TCP连接
	 * 
	 * @Title: createTcp
	 * @Description: TODO
	 * @throws IOException
	 *
	 */
	private static void createTcp() throws IOException {
		// 首先，添加对应的网络监听对象，等待处理连接和回复消息
		IoAcceptor ioAcceptor = new NioSocketAcceptor();
		// 添加过滤器
		ioAcceptor.getFilterChain().addLast("logger", new LoggingFilter());// 设置日志管理过滤器
		ioAcceptor.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));// 设置字节处理过滤器
		// 添加IOHandler
		ioAcceptor.setHandler(new MainServerHandler());
		// 添加Session配置
		ioAcceptor.getSessionConfig().setReadBufferSize(2048);// 设置读缓存区的大小
		ioAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);// 设置读写的空闲时间都是10秒
		ioAcceptor.bind(new InetSocketAddress(PORT));

	}

	/**
	 * 逻辑处理类
	 * 
	 * @Copy：2017-fmblzf
	 * @ProjectName：MINA_SERVER
	 * 
	 * @ClassDecription：
	 * @ClassName：com.fmblzf.minaservice.MainServerHandler
	 * @Creator：fmblzf
	 * @CreatTime：2017年5月27日 下午12:41:08
	 * @FixPerson：fmblzf
	 * @FixTime：2017年5月27日 下午12:41:08
	 * @Tag：
	 * @version V1.0
	 *
	 */
	private static class MainServerHandler extends IoHandlerAdapter {
		/**
		 * 接收到消息
		 * 
		 * @see org.apache.mina.core.service.IoHandlerAdapter#messageReceived(org.apache.mina.core.session.IoSession,
		 *      java.lang.Object)
		 *
		 */
		@Override
		public void messageReceived(IoSession session, Object message)
				throws Exception {
			String str = message.toString();
			if (str.trim().equalsIgnoreCase("quit")) {
				session.closeNow();
				return;
			}
			Date date = new Date();
			session.write(date.toString());
			System.out.println("Message written...");
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status)
				throws Exception {
			System.out.println("IDLE " + session.getIdleCount(status));
		}

		/**
		 * 发送消息给客户端
		 * 
		 * @see org.apache.mina.core.service.IoHandlerAdapter#messageSent(org.apache.mina.core.session.IoSession,
		 *      java.lang.Object)
		 *
		 */
		@Override
		public void messageSent(IoSession session, Object message)
				throws Exception {
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception {
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
		}
	}

}
