from rest_framework.serializers import ModelSerializer, PrimaryKeyRelatedField, ListSerializer
from api.db.models import Cart, Product, Store, Inventory, Report, User


class UserSerializer(ModelSerializer):
    class Meta:
        model = User
        fields = ('id', 'username', 'password')


class CartItemSerializer(ModelSerializer):
    product = PrimaryKeyRelatedField(queryset=Product.objects.all())
    user = PrimaryKeyRelatedField(queryset=User.objects.all())

    class Meta:
        model = Cart
        fields = ('id', 'product', 'quantity', 'user')


class CartItemListSerializer(ListSerializer):
    def update(self, instance, validated_data):
        pass

    child = CartItemSerializer()


class ProductSerializer(ModelSerializer):
    class Meta:
        model = Product
        fields = ('id', 'barcode', 'name')


class StoreSerializer(ModelSerializer):
    class Meta:
        model = Store
        fields = ('id', 'name', 'location', 'password', 'punishment')


class InventorySerializer(ModelSerializer):
    product = PrimaryKeyRelatedField(queryset=Product.objects.all())
    store = PrimaryKeyRelatedField(queryset=Store.objects.all())

    class Meta:
        model = Inventory
        fields = ('id', 'store', 'product', 'price', 'quantity')


class ReportSerializer(ModelSerializer):
    store = PrimaryKeyRelatedField(queryset=Store.objects.all())
    product = PrimaryKeyRelatedField(queryset=Product.objects.all())

    class Meta:
        model = Report
        fields = ('id', 'store', 'product', 'real_price', 'predicted')
