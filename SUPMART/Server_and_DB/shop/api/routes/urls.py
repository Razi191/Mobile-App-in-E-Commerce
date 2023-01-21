from django.urls import path
from api.routes import store, product, inventory, user, cart, report

urlpatterns = [
    path('manager/store/', store.create_store),
    path('manager/store', store.get_store),
    path('manager/stores', store.get_stores),
    path('manager/store/<int:store_id>', store.get_store_by_id),
    path('manager/store/<int:store_id>/is_punished', store.is_punished_route),
    path('manager/store/<int:store_id>/punishments', store.get_store_punishments),
    path('manager/report', report.get_reports_by_store),
    path('manager/product/', product.create_product),
    path('manager/product', product.get_product_id_by_barcode),
    path('manager/product/details', product.get_product_barcode_by_id),
    path('manager/store/<int:store_id>/product', product.get_store_price),
    path('manager/inventory/', inventory.create_store_inventory_record),
    path('manager/inventory/store/<int:store_id>', inventory.get_inventory),
    path('manager/inventory/store/<int:store_id>/product/<int:product_id>', inventory.update_inventory_price_quantity),
    path('manager/inventory/store/<int:store_id>/product/<int:product_id>/d', inventory.delete_inventory_record),
    path('manager/products', product.get_products),
    path('user/', user.create_user),
    path('user/g/<str:username>', user.get_user),
    path('user/u/<str:username>', user.update_user),
    path('user/d/<str:username>', user.delete_user),
    path('user/cart/', cart.add_cart_item),
    path('user/cart/suggest/<int:user_id>', cart.suggest),
    path('user/cart/g/<int:user_id>', cart.get_cart),
    path('user/cart/u/<int:user_id>', cart.update_cart_quantity),
    path('user/cart/d/<int:user_id>/', cart.delete_cart),
    path('user/cart/d/<int:user_id>', cart.delete_cart_item),
    path('user/report', report.create_report),
    

]

