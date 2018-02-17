package com.example.demo.commons;

public interface IProjectionConverter<IN, OUT> {
	public OUT readConvert(IN input);
	public IN writeConvert(OUT output);
}