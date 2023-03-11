package com.cinemamod.bukkit.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PacketByteBufReimpl extends ByteBuf {

    private final ByteBuf source;

    public PacketByteBufReimpl(ByteBuf source) {
        this.source = source;
    }

    public PacketByteBufReimpl writeVarInt(int i) {
        while ((i & -128) != 0) {
            this.writeByte(i & 127 | 128);
            i >>>= 7;
        }

        this.writeByte(i);
        return this;
    }

    public int readVarInt() {
        int i = 0;
        int j = 0;

        byte b;
        do {
            b = this.readByte();
            i |= (b & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b & 128) == 128);

        return i;
    }

    public PacketByteBufReimpl writeByteArray(byte[] bs) {
        this.writeVarInt(bs.length);
        this.writeBytes(bs);
        return this;
    }

    public byte[] readByteArray() {
        return this.readByteArray(this.readableBytes());
    }

    public byte[] readByteArray(int i) {
        int j = this.readVarInt();
        if (j > i) {
            throw new DecoderException("ByteArray with size " + j + " is bigger than allowed " + i);
        } else {
            byte[] bs = new byte[j];
            this.readBytes(bs);
            return bs;
        }
    }

    public PacketByteBufReimpl writeUUID(UUID uUID) {
        this.writeLong(uUID.getMostSignificantBits());
        this.writeLong(uUID.getLeastSignificantBits());
        return this;
    }

    public UUID readUUID() {
        return new UUID(this.readLong(), this.readLong());
    }

    public String readString() {
        return this.readString(32767);
    }

    public String readString(int i) {
        int j = this.readVarInt();
        if (j > i * 4) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + i * 4 + ")");
        } else if (j < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            String string = this.toString(this.readerIndex(), j, StandardCharsets.UTF_8);
            this.readerIndex(this.readerIndex() + j);
            if (string.length() > i) {
                throw new DecoderException("The received string length is longer than maximum allowed (" + j + " > " + i + ")");
            } else {
                return string;
            }
        }
    }

    public PacketByteBufReimpl writeString(String string) {
        return this.writeString(string, 32767);
    }

    public PacketByteBufReimpl writeString(String string, int i) {
        byte[] bs = string.getBytes(StandardCharsets.UTF_8);
        if (bs.length > i) {
            throw new EncoderException("String too big (was " + bs.length + " bytes encoded, max " + i + ")");
        } else {
            this.writeVarInt(bs.length);
            this.writeBytes(bs);
            return this;
        }
    }

    public int capacity() {
        return this.source.capacity();
    }

    public ByteBuf capacity(int i) {
        return this.source.capacity(i);
    }

    public int maxCapacity() {
        return this.source.maxCapacity();
    }

    public ByteBufAllocator alloc() {
        return this.source.alloc();
    }

    public ByteOrder order() {
        return this.source.order();
    }

    public ByteBuf order(ByteOrder byteOrder) {
        return this.source.order(byteOrder);
    }

    public ByteBuf unwrap() {
        return this.source.unwrap();
    }

    public boolean isDirect() {
        return this.source.isDirect();
    }

    public boolean isReadOnly() {
        return this.source.isReadOnly();
    }

    public ByteBuf asReadOnly() {
        return this.source.asReadOnly();
    }

    public int readerIndex() {
        return this.source.readerIndex();
    }

    public ByteBuf readerIndex(int i) {
        return this.source.readerIndex(i);
    }

    public int writerIndex() {
        return this.source.writerIndex();
    }

    public ByteBuf writerIndex(int i) {
        return this.source.writerIndex(i);
    }

    public ByteBuf setIndex(int i, int j) {
        return this.source.setIndex(i, j);
    }

    public int readableBytes() {
        return this.source.readableBytes();
    }

    public int writableBytes() {
        return this.source.writableBytes();
    }

    public int maxWritableBytes() {
        return this.source.maxWritableBytes();
    }

    public boolean isReadable() {
        return this.source.isReadable();
    }

    public boolean isReadable(int i) {
        return this.source.isReadable(i);
    }

    public boolean isWritable() {
        return this.source.isWritable();
    }

    public boolean isWritable(int i) {
        return this.source.isWritable(i);
    }

    public ByteBuf clear() {
        return this.source.clear();
    }

    public ByteBuf markReaderIndex() {
        return this.source.markReaderIndex();
    }

    public ByteBuf resetReaderIndex() {
        return this.source.resetReaderIndex();
    }

    public ByteBuf markWriterIndex() {
        return this.source.markWriterIndex();
    }

    public ByteBuf resetWriterIndex() {
        return this.source.resetWriterIndex();
    }

    public ByteBuf discardReadBytes() {
        return this.source.discardReadBytes();
    }

    public ByteBuf discardSomeReadBytes() {
        return this.source.discardSomeReadBytes();
    }

    public ByteBuf ensureWritable(int i) {
        return this.source.ensureWritable(i);
    }

    public int ensureWritable(int i, boolean bl) {
        return this.source.ensureWritable(i, bl);
    }

    public boolean getBoolean(int i) {
        return this.source.getBoolean(i);
    }

    public byte getByte(int i) {
        return this.source.getByte(i);
    }

    public short getUnsignedByte(int i) {
        return this.source.getUnsignedByte(i);
    }

    public short getShort(int i) {
        return this.source.getShort(i);
    }

    public short getShortLE(int i) {
        return this.source.getShortLE(i);
    }

    public int getUnsignedShort(int i) {
        return this.source.getUnsignedShort(i);
    }

    public int getUnsignedShortLE(int i) {
        return this.source.getUnsignedShortLE(i);
    }

    public int getMedium(int i) {
        return this.source.getMedium(i);
    }

    public int getMediumLE(int i) {
        return this.source.getMediumLE(i);
    }

    public int getUnsignedMedium(int i) {
        return this.source.getUnsignedMedium(i);
    }

    public int getUnsignedMediumLE(int i) {
        return this.source.getUnsignedMediumLE(i);
    }

    public int getInt(int i) {
        return this.source.getInt(i);
    }

    public int getIntLE(int i) {
        return this.source.getIntLE(i);
    }

    public long getUnsignedInt(int i) {
        return this.source.getUnsignedInt(i);
    }

    public long getUnsignedIntLE(int i) {
        return this.source.getUnsignedIntLE(i);
    }

    public long getLong(int i) {
        return this.source.getLong(i);
    }

    public long getLongLE(int i) {
        return this.source.getLongLE(i);
    }

    public char getChar(int i) {
        return this.source.getChar(i);
    }

    public float getFloat(int i) {
        return this.source.getFloat(i);
    }

    public double getDouble(int i) {
        return this.source.getDouble(i);
    }

    public ByteBuf getBytes(int i, ByteBuf byteBuf) {
        return this.source.getBytes(i, byteBuf);
    }

    public ByteBuf getBytes(int i, ByteBuf byteBuf, int j) {
        return this.source.getBytes(i, byteBuf, j);
    }

    public ByteBuf getBytes(int i, ByteBuf byteBuf, int j, int k) {
        return this.source.getBytes(i, byteBuf, j, k);
    }

    public ByteBuf getBytes(int i, byte[] bs) {
        return this.source.getBytes(i, bs);
    }

    public ByteBuf getBytes(int i, byte[] bs, int j, int k) {
        return this.source.getBytes(i, bs, j, k);
    }

    public ByteBuf getBytes(int i, ByteBuffer byteBuffer) {
        return this.source.getBytes(i, byteBuffer);
    }

    public ByteBuf getBytes(int i, OutputStream outputStream, int j) throws IOException {
        return this.source.getBytes(i, outputStream, j);
    }

    public int getBytes(int i, GatheringByteChannel gatheringByteChannel, int j) throws IOException {
        return this.source.getBytes(i, gatheringByteChannel, j);
    }

    public int getBytes(int i, FileChannel fileChannel, long l, int j) throws IOException {
        return this.source.getBytes(i, fileChannel, l, j);
    }

    public CharSequence getCharSequence(int i, int j, Charset charset) {
        return this.source.getCharSequence(i, j, charset);
    }

    public ByteBuf setBoolean(int i, boolean bl) {
        return this.source.setBoolean(i, bl);
    }

    public ByteBuf setByte(int i, int j) {
        return this.source.setByte(i, j);
    }

    public ByteBuf setShort(int i, int j) {
        return this.source.setShort(i, j);
    }

    public ByteBuf setShortLE(int i, int j) {
        return this.source.setShortLE(i, j);
    }

    public ByteBuf setMedium(int i, int j) {
        return this.source.setMedium(i, j);
    }

    public ByteBuf setMediumLE(int i, int j) {
        return this.source.setMediumLE(i, j);
    }

    public ByteBuf setInt(int i, int j) {
        return this.source.setInt(i, j);
    }

    public ByteBuf setIntLE(int i, int j) {
        return this.source.setIntLE(i, j);
    }

    public ByteBuf setLong(int i, long l) {
        return this.source.setLong(i, l);
    }

    public ByteBuf setLongLE(int i, long l) {
        return this.source.setLongLE(i, l);
    }

    public ByteBuf setChar(int i, int j) {
        return this.source.setChar(i, j);
    }

    public ByteBuf setFloat(int i, float f) {
        return this.source.setFloat(i, f);
    }

    public ByteBuf setDouble(int i, double d) {
        return this.source.setDouble(i, d);
    }

    public ByteBuf setBytes(int i, ByteBuf byteBuf) {
        return this.source.setBytes(i, byteBuf);
    }

    public ByteBuf setBytes(int i, ByteBuf byteBuf, int j) {
        return this.source.setBytes(i, byteBuf, j);
    }

    public ByteBuf setBytes(int i, ByteBuf byteBuf, int j, int k) {
        return this.source.setBytes(i, byteBuf, j, k);
    }

    public ByteBuf setBytes(int i, byte[] bs) {
        return this.source.setBytes(i, bs);
    }

    public ByteBuf setBytes(int i, byte[] bs, int j, int k) {
        return this.source.setBytes(i, bs, j, k);
    }

    public ByteBuf setBytes(int i, ByteBuffer byteBuffer) {
        return this.source.setBytes(i, byteBuffer);
    }

    public int setBytes(int i, InputStream inputStream, int j) throws IOException {
        return this.source.setBytes(i, inputStream, j);
    }

    public int setBytes(int i, ScatteringByteChannel scatteringByteChannel, int j) throws IOException {
        return this.source.setBytes(i, scatteringByteChannel, j);
    }

    public int setBytes(int i, FileChannel fileChannel, long l, int j) throws IOException {
        return this.source.setBytes(i, fileChannel, l, j);
    }

    public ByteBuf setZero(int i, int j) {
        return this.source.setZero(i, j);
    }

    public int setCharSequence(int i, CharSequence charSequence, Charset charset) {
        return this.source.setCharSequence(i, charSequence, charset);
    }

    public boolean readBoolean() {
        return this.source.readBoolean();
    }

    public byte readByte() {
        return this.source.readByte();
    }

    public short readUnsignedByte() {
        return this.source.readUnsignedByte();
    }

    public short readShort() {
        return this.source.readShort();
    }

    public short readShortLE() {
        return this.source.readShortLE();
    }

    public int readUnsignedShort() {
        return this.source.readUnsignedShort();
    }

    public int readUnsignedShortLE() {
        return this.source.readUnsignedShortLE();
    }

    public int readMedium() {
        return this.source.readMedium();
    }

    public int readMediumLE() {
        return this.source.readMediumLE();
    }

    public int readUnsignedMedium() {
        return this.source.readUnsignedMedium();
    }

    public int readUnsignedMediumLE() {
        return this.source.readUnsignedMediumLE();
    }

    public int readInt() {
        return this.source.readInt();
    }

    public int readIntLE() {
        return this.source.readIntLE();
    }

    public long readUnsignedInt() {
        return this.source.readUnsignedInt();
    }

    public long readUnsignedIntLE() {
        return this.source.readUnsignedIntLE();
    }

    public long readLong() {
        return this.source.readLong();
    }

    public long readLongLE() {
        return this.source.readLongLE();
    }

    public char readChar() {
        return this.source.readChar();
    }

    public float readFloat() {
        return this.source.readFloat();
    }

    public double readDouble() {
        return this.source.readDouble();
    }

    public ByteBuf readBytes(int i) {
        return this.source.readBytes(i);
    }

    public ByteBuf readSlice(int i) {
        return this.source.readSlice(i);
    }

    public ByteBuf readRetainedSlice(int i) {
        return this.source.readRetainedSlice(i);
    }

    public ByteBuf readBytes(ByteBuf byteBuf) {
        return this.source.readBytes(byteBuf);
    }

    public ByteBuf readBytes(ByteBuf byteBuf, int i) {
        return this.source.readBytes(byteBuf, i);
    }

    public ByteBuf readBytes(ByteBuf byteBuf, int i, int j) {
        return this.source.readBytes(byteBuf, i, j);
    }

    public ByteBuf readBytes(byte[] bs) {
        return this.source.readBytes(bs);
    }

    public ByteBuf readBytes(byte[] bs, int i, int j) {
        return this.source.readBytes(bs, i, j);
    }

    public ByteBuf readBytes(ByteBuffer byteBuffer) {
        return this.source.readBytes(byteBuffer);
    }

    public ByteBuf readBytes(OutputStream outputStream, int i) throws IOException {
        return this.source.readBytes(outputStream, i);
    }

    public int readBytes(GatheringByteChannel gatheringByteChannel, int i) throws IOException {
        return this.source.readBytes(gatheringByteChannel, i);
    }

    public CharSequence readCharSequence(int i, Charset charset) {
        return this.source.readCharSequence(i, charset);
    }

    public int readBytes(FileChannel fileChannel, long l, int i) throws IOException {
        return this.source.readBytes(fileChannel, l, i);
    }

    public ByteBuf skipBytes(int i) {
        return this.source.skipBytes(i);
    }

    public ByteBuf writeBoolean(boolean bl) {
        return this.source.writeBoolean(bl);
    }

    public ByteBuf writeByte(int i) {
        return this.source.writeByte(i);
    }

    public ByteBuf writeShort(int i) {
        return this.source.writeShort(i);
    }

    public ByteBuf writeShortLE(int i) {
        return this.source.writeShortLE(i);
    }

    public ByteBuf writeMedium(int i) {
        return this.source.writeMedium(i);
    }

    public ByteBuf writeMediumLE(int i) {
        return this.source.writeMediumLE(i);
    }

    public ByteBuf writeInt(int i) {
        return this.source.writeInt(i);
    }

    public ByteBuf writeIntLE(int i) {
        return this.source.writeIntLE(i);
    }

    public ByteBuf writeLong(long l) {
        return this.source.writeLong(l);
    }

    public ByteBuf writeLongLE(long l) {
        return this.source.writeLongLE(l);
    }

    public ByteBuf writeChar(int i) {
        return this.source.writeChar(i);
    }

    public ByteBuf writeFloat(float f) {
        return this.source.writeFloat(f);
    }

    public ByteBuf writeDouble(double d) {
        return this.source.writeDouble(d);
    }

    public ByteBuf writeBytes(ByteBuf byteBuf) {
        return this.source.writeBytes(byteBuf);
    }

    public ByteBuf writeBytes(ByteBuf byteBuf, int i) {
        return this.source.writeBytes(byteBuf, i);
    }

    public ByteBuf writeBytes(ByteBuf byteBuf, int i, int j) {
        return this.source.writeBytes(byteBuf, i, j);
    }

    public ByteBuf writeBytes(byte[] bs) {
        return this.source.writeBytes(bs);
    }

    public ByteBuf writeBytes(byte[] bs, int i, int j) {
        return this.source.writeBytes(bs, i, j);
    }

    public ByteBuf writeBytes(ByteBuffer byteBuffer) {
        return this.source.writeBytes(byteBuffer);
    }

    public int writeBytes(InputStream inputStream, int i) throws IOException {
        return this.source.writeBytes(inputStream, i);
    }

    public int writeBytes(ScatteringByteChannel scatteringByteChannel, int i) throws IOException {
        return this.source.writeBytes(scatteringByteChannel, i);
    }

    public int writeBytes(FileChannel fileChannel, long l, int i) throws IOException {
        return this.source.writeBytes(fileChannel, l, i);
    }

    public ByteBuf writeZero(int i) {
        return this.source.writeZero(i);
    }

    public int writeCharSequence(CharSequence charSequence, Charset charset) {
        return this.source.writeCharSequence(charSequence, charset);
    }

    public int indexOf(int i, int j, byte b) {
        return this.source.indexOf(i, j, b);
    }

    public int bytesBefore(byte b) {
        return this.source.bytesBefore(b);
    }

    public int bytesBefore(int i, byte b) {
        return this.source.bytesBefore(i, b);
    }

    public int bytesBefore(int i, int j, byte b) {
        return this.source.bytesBefore(i, j, b);
    }

    public int forEachByte(ByteProcessor byteProcessor) {
        return this.source.forEachByte(byteProcessor);
    }

    public int forEachByte(int i, int j, ByteProcessor byteProcessor) {
        return this.source.forEachByte(i, j, byteProcessor);
    }

    public int forEachByteDesc(ByteProcessor byteProcessor) {
        return this.source.forEachByteDesc(byteProcessor);
    }

    public int forEachByteDesc(int i, int j, ByteProcessor byteProcessor) {
        return this.source.forEachByteDesc(i, j, byteProcessor);
    }

    public ByteBuf copy() {
        return this.source.copy();
    }

    public ByteBuf copy(int i, int j) {
        return this.source.copy(i, j);
    }

    public ByteBuf slice() {
        return this.source.slice();
    }

    public ByteBuf retainedSlice() {
        return this.source.retainedSlice();
    }

    public ByteBuf slice(int i, int j) {
        return this.source.slice(i, j);
    }

    public ByteBuf retainedSlice(int i, int j) {
        return this.source.retainedSlice(i, j);
    }

    public ByteBuf duplicate() {
        return this.source.duplicate();
    }

    public ByteBuf retainedDuplicate() {
        return this.source.retainedDuplicate();
    }

    public int nioBufferCount() {
        return this.source.nioBufferCount();
    }

    public ByteBuffer nioBuffer() {
        return this.source.nioBuffer();
    }

    public ByteBuffer nioBuffer(int i, int j) {
        return this.source.nioBuffer(i, j);
    }

    public ByteBuffer internalNioBuffer(int i, int j) {
        return this.source.internalNioBuffer(i, j);
    }

    public ByteBuffer[] nioBuffers() {
        return this.source.nioBuffers();
    }

    public ByteBuffer[] nioBuffers(int i, int j) {
        return this.source.nioBuffers(i, j);
    }

    public boolean hasArray() {
        return this.source.hasArray();
    }

    public byte[] array() {
        return this.source.array();
    }

    public int arrayOffset() {
        return this.source.arrayOffset();
    }

    public boolean hasMemoryAddress() {
        return this.source.hasMemoryAddress();
    }

    public long memoryAddress() {
        return this.source.memoryAddress();
    }

    public String toString(Charset charset) {
        return this.source.toString(charset);
    }

    public String toString(int i, int j, Charset charset) {
        return this.source.toString(i, j, charset);
    }

    public int hashCode() {
        return this.source.hashCode();
    }

    public boolean equals(Object object) {
        return this.source.equals(object);
    }

    public int compareTo(ByteBuf byteBuf) {
        return this.source.compareTo(byteBuf);
    }

    public String toString() {
        return this.source.toString();
    }

    public ByteBuf retain(int i) {
        return this.source.retain(i);
    }

    public ByteBuf retain() {
        return this.source.retain();
    }

    public ByteBuf touch() {
        return this.source.touch();
    }

    public ByteBuf touch(Object object) {
        return this.source.touch(object);
    }

    public int refCnt() {
        return this.source.refCnt();
    }

    public boolean release() {
        return this.source.release();
    }

    public boolean release(int i) {
        return this.source.release(i);
    }

}
