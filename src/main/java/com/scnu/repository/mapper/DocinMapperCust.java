package com.scnu.repository.mapper;

import org.apache.ibatis.annotations.Param;

public interface DocinMapperCust {

    public void increaseViewCount(@Param("id") Long id);

    public void increaseVoteCount(@Param("id") Long id);
//
    public void updateEbookInfo();
}
