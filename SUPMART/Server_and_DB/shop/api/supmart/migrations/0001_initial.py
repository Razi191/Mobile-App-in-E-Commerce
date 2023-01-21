# Generated by Django 4.1.4 on 2023-01-17 21:26

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Product',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False)),
                ('barcode', models.TextField(unique=True)),
                ('name', models.TextField(unique=True)),
            ],
        ),
        migrations.CreateModel(
            name='User',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False)),
                ('username', models.TextField(unique=True)),
                ('password', models.TextField()),
            ],
            options={
                'unique_together': {('username', 'password')},
            },
        ),
        migrations.CreateModel(
            name='Store',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False)),
                ('name', models.TextField()),
                ('location', models.TextField()),
                ('password', models.TextField()),
                ('punishment', models.IntegerField(default=0)),
            ],
            options={
                'unique_together': {('name', 'location')},
            },
        ),
        migrations.CreateModel(
            name='Report',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False)),
                ('real_price', models.FloatField()),
                ('product', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='supmart.product')),
                ('store', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='supmart.store')),
            ],
        ),
        migrations.CreateModel(
            name='Inventory',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False)),
                ('price', models.FloatField()),
                ('quantity', models.IntegerField()),
                ('product', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='supmart.product')),
                ('store', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='supmart.store')),
            ],
            options={
                'unique_together': {('store', 'product')},
            },
        ),
        migrations.CreateModel(
            name='Cart',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False)),
                ('quantity', models.PositiveIntegerField()),
                ('product', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='supmart.product')),
                ('user', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='supmart.user')),
            ],
            options={
                'unique_together': {('user', 'product')},
            },
        ),
    ]
