package com.scnu.repository.mapper;

import com.scnu.repository.domain.Docin;
import com.scnu.repository.domain.DocinExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DocinMapper {
    long countByExample(DocinExample example);

    int deleteByExample(DocinExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Docin record);

    int insertSelective(Docin record);

    List<Docin> selectByExample(DocinExample example);

    Docin selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Docin record, @Param("example") DocinExample example);

    int updateByExample(@Param("record") Docin record, @Param("example") DocinExample example);

    int updateByPrimaryKeySelective(Docin record);

    int updateByPrimaryKey(Docin record);
}