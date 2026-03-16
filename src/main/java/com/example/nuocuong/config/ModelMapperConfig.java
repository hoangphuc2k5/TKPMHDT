package com.example.nuocuong.config;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper mm = new ModelMapper();
		mm.getConfiguration()
			.setFieldMatchingEnabled(true)
			.setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
			.setPropertyCondition(Conditions.isNotNull());
		return mm;
	}
}

