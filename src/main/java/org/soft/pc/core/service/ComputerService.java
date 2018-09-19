package org.soft.pc.core.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.soft.pc.core.exception.PC404Exception;
import org.soft.pc.core.exception.PC4XXException;
import org.soft.pc.core.mapper.MbgComputerMapper;
import org.soft.pc.core.model.MbgComputer;
import org.soft.pc.core.model.MbgComputerExample;
import org.soft.pc.core.model.MbgComputerForm;
import org.soft.pc.core.model.MbgComputerUpdateForm;
import org.soft.pc.core.model.PCPager;
import org.soft.pc.mgt.common.SoftJsonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;

@Transactional(value = "transactionManager", rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED, timeout = 300)
@Service
public class ComputerService {

	@Autowired
	private MbgComputerMapper mbgComputerMapper;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Value("${PC_ID_REDIS}")
	private String PC_ID_REDIS;

	// 返回所有的Computer数组
	public PCPager<MbgComputer> getAllComputers(String pageSize, String pageNum) {

		PCPager<MbgComputer> pcPager = new PCPager<>();
		if(!StringUtils.isEmpty(pageSize) && !StringUtils.isEmpty(pageNum)) {
			PageHelper.startPage(Integer.parseInt(pageNum), Integer.parseInt(pageSize));
			pcPager.setPageNum(Integer.parseInt(pageNum));
		}
		List<MbgComputer> mbgComputerList = mbgComputerMapper.selectByExample(null);
		pcPager.setData(mbgComputerList);
		
		Long totalRows = mbgComputerMapper.countByExample(null);
		pcPager.setTotalRows(totalRows);
		return pcPager;
	}

	public String deleteById(String cid) {
		mbgComputerMapper.deleteByPrimaryKey(Integer.parseInt(cid));
		return "Success";
	}

	public void add(@Valid MbgComputerForm mbgComputerForm) {

		MbgComputer mbgComputer = new MbgComputer();
		
		String tradeMark = mbgComputerForm.getTrademark();
		
		MbgComputerExample mbgComputerExa = new MbgComputerExample();
		MbgComputerExample.Criteria mbgComputerCri = mbgComputerExa.createCriteria();
		mbgComputerCri.andTrademarkEqualTo(tradeMark);
		List<MbgComputer> mbgComputerList = mbgComputerMapper.selectByExample(mbgComputerExa);
		if(mbgComputerList.size() > 0) {
			throw new PC4XXException("PC Already Exist");
		}
		
		BeanUtils.copyProperties(mbgComputerForm, mbgComputer);
		mbgComputerMapper.insert(mbgComputer);
	}

	public MbgComputer queryComputerById(String cid) {
		
		String key = PC_ID_REDIS + ":" + cid;
		
		//判断是否在Redis中
		if(stringRedisTemplate.hasKey(key)) {
			String pcJson = stringRedisTemplate.opsForValue().get(key);
			System.out.println("从redis中取出数据");
			
			//重置过期时间
			stringRedisTemplate.expire(key, 60, TimeUnit.MINUTES);
			
			//需要将JSON字符串转化为MbgComputer对象
			return SoftJsonUtil.jsonToPojo(pcJson, MbgComputer.class);
		}
		//Redis中没有查到的话则查询数据库
		MbgComputerExample mbgComputerExa = new MbgComputerExample();
		MbgComputerExample.Criteria mbgComputerCri = mbgComputerExa.createCriteria();
		mbgComputerCri.andIdEqualTo(Integer.parseInt(cid));
		List<MbgComputer> mbgComList = mbgComputerMapper.selectByExample(mbgComputerExa);
		if(mbgComList.size() < 0) {
			throw new PC404Exception("PC Not Found");
		}
		//将MbgComputer转化为JSON字符串然后存进Redis
		MbgComputer mbgComputer = mbgComList.get(0);
		String pcJson = SoftJsonUtil.objectToJson(mbgComputer);
		
		System.out.println("从数据库中查询数据");
		
		//然后将数据写入Redis
		stringRedisTemplate.opsForValue().set(key, pcJson, 60, TimeUnit.MINUTES);
		return mbgComputer;
	}

	public void update(@Valid MbgComputerUpdateForm mbgComputerUpdateForm) {
		Integer cid = mbgComputerUpdateForm.getId();
		MbgComputer mbgComputer = mbgComputerMapper.selectByPrimaryKey(cid);
		
		if(mbgComputer == null) {
			throw new PC404Exception("PC Not Found");
		}
		mbgComputerUpdateForm.setPic(mbgComputer.getPic());
		//如果存在则进行商品更新
		BeanUtils.copyProperties(mbgComputerUpdateForm, mbgComputer);
		mbgComputerMapper.updateByPrimaryKey(mbgComputer);
		//拼接Redis中的key
		String key = PC_ID_REDIS + ":" + cid;
		//判断是否在redis中，如果存在
		if(stringRedisTemplate.hasKey(key)) {
			//将商品对象转化为JSON
			String pcJson = SoftJsonUtil.objectToJson(mbgComputer);
			//然后将数据更新进redis
			stringRedisTemplate.opsForValue().set(key, pcJson, 60, TimeUnit.MINUTES);
		}
	}
}
