package com.payal.ecom.services.admin.faq;

import com.payal.ecom.dto.FAQDto;

public interface FAQService {

    FAQDto postFAQ(Long productId, FAQDto faqDto);
}
