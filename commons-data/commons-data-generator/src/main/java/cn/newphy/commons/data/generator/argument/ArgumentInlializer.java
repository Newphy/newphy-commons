package cn.newphy.commons.data.generator.argument;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.newphy.commons.data.generator.CRUDDao.ExistException;
import cn.newphy.commons.data.generator.argument.service.ArgService;
import cn.newphy.commons.data.generator.model.arg.Argument;
import cn.newphy.commons.data.generator.utils.DateUtils;

@Component
public class ArgumentInlializer {

	@Autowired
	private ArgService argumentService;
	
	public void init() {
		List<Argument> args = getDefaultArguments();
		for (Argument arg : args) {
			try {
				argumentService.addArgument(arg);
			} catch (ExistException e) {
			}
		}
	}

	private List<Argument> getDefaultArguments() {
		List<Argument> args = new ArrayList<Argument>();
		args.add(getDateArgument());
		args.add(getDateTimeArgument());
		args.add(getAuthorArgument());
		return args;
	}
	
	
	private Argument getAuthorArgument() {
		Argument author = new Argument();
		author.setName("author");
		author.setType(1);
		author.setValue(System.getenv().get("USERNAME"));
		author.setDescription("作者名字");
		return author;
	}
	
	
	private Argument getDateArgument() {
		@SuppressWarnings("serial")
		Argument dateArg = new Argument(){
			@Override
			public String getValue() {
				return DateUtils.format(new Date(), "yyyy-MM-dd");
			}
		};
		dateArg.setName("date");
		dateArg.setType(2);
		dateArg.setDescription("日期参数");
		return dateArg;
	}
	
	
	private Argument getDateTimeArgument() {
		@SuppressWarnings("serial")
		Argument datetimeArg = new Argument(){
			@Override
			public String getValue() {
				return DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
			}
		};
		datetimeArg.setName("datetime");
		datetimeArg.setType(2);
		datetimeArg.setDescription("日期时间参数");
		return datetimeArg;
	}
}
