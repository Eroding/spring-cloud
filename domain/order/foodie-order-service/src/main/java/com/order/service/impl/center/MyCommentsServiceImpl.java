package com.order.service.impl.center;

import com.enums.YesOrNo;
import com.order.fallback.itemservice.ItemCommentsFeignClient;
import com.order.mapper.OrderItemsMapper;
import com.order.mapper.OrderStatusMapper;
import com.order.mapper.OrdersMapper;
import com.order.pojo.OrderItems;
import com.order.pojo.OrderStatus;
import com.order.pojo.Orders;
import com.order.pojo.bo.center.OrderItemsCommentBO;
import com.order.service.center.MyCommentsService;
import com.service.BaseService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MyCommentsServiceImpl extends BaseService implements MyCommentsService {

    @Autowired
    public OrderItemsMapper orderItemsMapper;

    @Autowired
    public OrdersMapper ordersMapper;

    @Autowired
    public OrderStatusMapper orderStatusMapper;

    @Autowired
    public ItemCommentsFeignClient itemsCommentsMapperCustom;
    // TODO feign章节里改成item-api


    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<OrderItems> queryPendingComment(String orderId) {
        OrderItems query = new OrderItems();
        query.setOrderId(orderId);
        return orderItemsMapper.select(query);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComments(String orderId, String userId,
                             List<OrderItemsCommentBO> commentList) {

        // 1. 保存评价 items_comments
        for (OrderItemsCommentBO oic : commentList) {
            oic.setCommentId(sid.nextShort());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("commentList", commentList);
        itemsCommentsMapperCustom.saveComments(map);



        // 2. 修改订单表改已评价 orders
        Orders order = new Orders();
        order.setId(orderId);
        order.setIsComment(YesOrNo.YES.type);
        ordersMapper.updateByPrimaryKeySelective(order);

        // 3. 修改订单状态表的留言时间 order_status
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }

    // TODO 移到了itemCommentService
//    @Transactional(propagation = Propagation.SUPPORTS)
//    @Override
//    public PagedGridResult queryMyComments(String userId,
//                                           Integer page,
//                                           Integer pageSize) {
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("userId", userId);
//
//        PageHelper.startPage(page, pageSize);
//        List<MyCommentVO> list = itemsCommentsMapperCustom.queryMyComments(map);
//
//        return setterPagedGrid(list, page);
//    }
}
