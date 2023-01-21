from django.db import models


class User(models.Model):
    id = models.AutoField(primary_key=True)
    username = models.TextField(unique=True)
    password = models.TextField()

    objects = models.Manager()

    class Meta:
        app_label = 'supmart'
        unique_together = [['username', 'password']]


class Product(models.Model):
    id = models.AutoField(primary_key=True)
    barcode = models.TextField(unique=True)
    name = models.TextField(unique=True)

    objects = models.Manager()

    class Meta:
        app_label = 'supmart'



class Cart(models.Model):
    id = models.AutoField(primary_key=True)
    product = models.ForeignKey(Product, on_delete=models.CASCADE)
    quantity = models.PositiveIntegerField()
    user = models.ForeignKey(User, on_delete=models.CASCADE)

    objects = models.Manager()

    class Meta:
        app_label = 'supmart'
        unique_together = [['user', 'product']]


class Store(models.Model):
    id = models.AutoField(primary_key=True)
    name = models.TextField()
    location = models.TextField()
    password = models.TextField()
    punishment = models.IntegerField(default=0)

    objects = models.Manager()

    class Meta:
        app_label = 'supmart'
        unique_together = [['name', 'location']]


class Inventory(models.Model):
    id = models.AutoField(primary_key=True)
    store = models.ForeignKey(Store, on_delete=models.CASCADE)
    product = models.ForeignKey(Product, on_delete=models.CASCADE)
    price = models.FloatField()
    quantity = models.IntegerField()

    objects = models.Manager()

    class Meta:
        app_label = 'supmart'
        unique_together = [['store', 'product']]


class Report(models.Model):
    id = models.AutoField(primary_key=True)
    store = models.ForeignKey(Store, on_delete=models.CASCADE)
    product = models.ForeignKey(Product, on_delete=models.CASCADE)
    real_price = models.FloatField()
    predicted = models.FloatField(default=0)

    objects = models.Manager()

    class Meta:
        app_label = 'supmart'
