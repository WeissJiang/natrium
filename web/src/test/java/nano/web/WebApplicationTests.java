package nano.web;

import com.github.houbb.nlp.common.segment.impl.CommonSegments;
import com.github.houbb.pinyin.bs.PinyinBs;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.support.segment.PinyinSegments;
import com.github.houbb.pinyin.util.PinyinHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@SpringBootTest
class WebApplicationTests {

    @Autowired
    public ApplicationContext context;

    @Test
    void contextLoads() {
        var zhText = "我能吞下玻璃而不受伤";
        var segments = CommonSegments.simple().segment(zhText);
        var pinyinList = Arrays.stream(PinyinHelper.toPinyin(zhText).split(" ")).toList();
        System.out.println(segments);
        System.out.println(segments.size());
        System.out.println(pinyinList);
        System.out.println(pinyinList.size());
    }

}
