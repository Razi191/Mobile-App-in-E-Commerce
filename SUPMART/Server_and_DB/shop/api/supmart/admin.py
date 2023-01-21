from django.contrib import admin

# Register your models here.

from api.db.models import User, Cart, Store, Product, Inventory, Report

admin.site.register(User)
admin.site.register(Cart)
admin.site.register(Store)
admin.site.register(Product)
admin.site.register(Inventory)
admin.site.register(Report)
