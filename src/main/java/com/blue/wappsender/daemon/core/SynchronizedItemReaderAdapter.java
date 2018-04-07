package com.blue.wappsender.daemon.core;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class SynchronizedItemReaderAdapter<T> implements ItemReader<T>, ItemStream
{
	private final ItemReader<T> _delegate;

	public SynchronizedItemReaderAdapter(ItemReader<T> pDelegate)
	{
		_delegate = pDelegate;
	}
	
	@Override
	public synchronized T read() throws Exception, UnexpectedInputException, ParseException
	{
		return _delegate.read();
	}

	@Override
	public synchronized void close() throws ItemStreamException
	{
		if (_delegate instanceof ItemStream)
		{
			((ItemStream)_delegate).close();
		}
	}

	@Override
	public synchronized void open(ExecutionContext pExecutionContext) throws ItemStreamException
	{
		if (_delegate instanceof ItemStream)
		{
			((ItemStream)_delegate).open(pExecutionContext);
		}
	}

	@Override
	public synchronized void update(ExecutionContext pExecutionContext) throws ItemStreamException
	{
		if (_delegate instanceof ItemStream)
		{
			((ItemStream)_delegate).update(pExecutionContext);
		}
	}

}
