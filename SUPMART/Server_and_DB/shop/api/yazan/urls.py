# from django.urls import path
# from . import views
# from . import manager
#
# urlpatterns = [
#     path('', views.getRoutes),
#     path('watad/', views.getWatad),
#     path('Cart/', views.getCart),
#     path('Cart/getstores/', views.getStores),
#     path('Cart/getstores/', views.getStores),
#     path('Cart/getprice/<str:store_id>/<str:barCode>/', views.getPrice),
#     path('Cart/add/<str:store_id>/<str:barCode>/<int:count>/', views.addItem),
#     path('Cart/update/<str:pk>/', views.updateItem),
#     path('Cart/delete/<str:pk>/', views.deleteItem),
#     path('Cart/<str:pk>/', views.getItem),
#     path('Cart/reportprice/<str:store_id>/<str:barCode>/<int:price>/', views.reportPrice),
#     path('Cart/suggest/<str:current_store_id>/', views.suggest),
#     path('Manager/setprice/<str:store_id>/<str:barCode>/<int:price>/', manager.set_price),
#     path('Manager/addstore/<str:store_name>/<str:location_id>/<str:password>/', manager.add_store),
#     path('Manager/signin/<str:store_id>/<str:password>/', manager.signIn),
#     path('Manager/viewwarnings/<str:store_id>/', manager.viewWarnings),
#     path('Manager/deleteitem/<str:store_id>/<str:barCode>/', manager.deleteItem),
#     path('Manager/additem/<str:store_id>/<str:barCode>/<int:price>/<str:name>/', manager.addItem),
#     path('Manager/viewitems/<str:store_id>/', manager.viewItems),
#     path('Manager/viewreports/<str:store_id>/', manager.viewReports),
# ]
