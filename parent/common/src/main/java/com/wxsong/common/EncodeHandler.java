package com.wxsong.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class EncodeHandler extends MessageToByteEncoder {

	private Class<?> genericClass;
	
	public EncodeHandler(Class<?> genericClass) {
		this.genericClass = genericClass;
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Object obj, ByteBuf out) throws Exception {
		if(genericClass.isInstance(obj)) {
			byte[] bytes = SerializationUtil.serialize(obj);
			out.writeInt(bytes.length);
			out.writeBytes(bytes);
		}
	}

}
