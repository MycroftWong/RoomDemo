# Room 实践

彻底阅读了官方的`Room`文档，实现了部分功能

## 简介

`Room`提供SQLite的抽象层，实际上就是封装了SQLite, 同时使用注解，自动生成代码，这一点和`DBFlow`很像，估计也是借鉴了的。

### Room三大组件
* Database 持有数据库，保持和数据库的连接，使用`Room.databaseBuilder`或`Room.inMemoryDatabaseBuilder()`创建
* Entity 代表数据库中的表`table`
* DAO 包含操作数据库的方法

<!--![Room架构图](https://developer.android.com/images/training/data-storage/room_architecture.png)-->

## 使用

使用注解定义`Database`, `Entity`, `DAO`

`Database`定义为抽象类或接口

`Entity`中主键使用`PrimaryKey`标注...

`DAO`定义为接口，其中的所有方法需要使用`Query`, `Delete`, `Insert`, `Update`, `RawQuery`标注

## Entity

具体参考
[Province.java](https://github.com/MycroftWong/RoomDemo/app/src/main/java/com/mycroft/roomdeomo/entity/Province.java),
[City.java](https://github.com/MycroftWong/RoomDemo/app/src/main/java/com/mycroft/roomdeomo/entity/City.java),
[County.java](https://github.com/MycroftWong/RoomDemo/app/src/main/java/com/mycroft/roomdeomo/entity/County.java),
[Street.java](https://github.com/MycroftWong/RoomDemo/app/src/main/java/com/mycroft/roomdeomo/entity/Street.java)

如下是`Street.java`实现，使用了`Entity`, `PrimaryKey`, 
在标注`Entity`中，指定了表名，外键，索引。外键和索引可以有多个 
```java
@Entity(tableName = "street",
        foreignKeys = @ForeignKey(entity = County.class, parentColumns = "id", childColumns = "county_id"),
        indices = @Index("county_id"))
public class Street {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;

    @SerializedName("county_id")
    @ColumnInfo(name = "county_id")
    public int countyId;

    @ColumnInfo(name = "name")
    public String name;

    public Street() {
    }

    @Ignore
    public Street(int id, int countyId, String name) {
        this.id = id;
        this.countyId = countyId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCountyId() {
        return countyId;
    }

    public void setCountyId(int countyId) {
        this.countyId = countyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

`AddressDao.java`实现
```java
@Dao
public interface AddressDao {
    @Query("SELECT * FROM province")
    List<Province> loadAllProvinces();
    // ...
}
``` 
 
`AddressDatabase.java`实现
```java
@Database(entities = {Province.class, City.class, County.class, Street.class},
        views = {StreetDetailInfo.class},
        version = 1,
        exportSchema = false)
public abstract class AddressDatabase extends RoomDatabase {
    public abstract AddressDao addressDao();
}
```

### Embedded的使用

在`Entity`的类中，使用`Embedded`标注的字段，可以是一个自定义的类，实际上可以认为是`Entity`的扩展属性。
如下，`User`表中，实际上有`id`, `firstName`, `street`, `state`, `city`, `post_code`五个字段。
```kotlin
data class Address(
    val street: String?,
    val state: String?,
    val city: String?,
    @ColumnInfo(name = "post_code") val postCode: Int
)

@Entity
data class User(
    @PrimaryKey val id: Int,
    val firstName: String?,
    @Embedded val address: Address?
)
```  

### Ignore的使用

在`Entity`中，使用`Ignore`标注的字段在写入数据库中时会被忽略，就像`Gson`中的`Export`

### AutoValue的使用

不能使用`kotlin`, 只能使用`java`构造抽象类，使用如下
```java
@AutoValue
@Entity
public abstract class User {
    // Supported annotations must include `@CopyAnnotations`.
    @CopyAnnotations
    @PrimaryKey
    public abstract long getId();

    public abstract String getFirstName();
    public abstract String getLastName();

    // Room uses this factory method to create User objects.
    public static User create(long id, String firstName, String lastName) {
        return new AutoValue_User(id, firstName, lastName);
    }
}
```

### many-to-many 多对多关系

建第三张表，两个字段是外键，指向关联的两张表。不详细讲解

## DatabaseView 数据库视图

引用自百度百科
> 视图是从一个或几个基本表（或视图）中导出的虚拟的表。在系统的数据字典中仅存放了视图的定义，不存放视图对应的数据。

> 视图是原始数据库数据的一种变换，是查看表中数据的另外一种方式。可以将视图看成是一个移动的窗口，通过它可以看到感兴趣的数据。 视图是从一个或多个实际表中获得的，这些表的数据存放在数据库中。那些用于产生视图的表叫做该视图的基表。一个视图也可以从另一个视图中产生。

数据库视图实际上就是一张虚拟表，简化查询（不能进行增、删、改操作），在频繁使用链表查询的时候非常有用，实现可参考[StreetDetailInfo.kt](https://github.com/MycroftWong/RoomDemo/app/src/main/java/com/mycroft/roomdeomo/entity/StreetDetailInfo.kt),

在`Database`标注中添加该视图，然后在`DAO`中可以将数据库视图作为一张表进行查询

```java
@Database(entities = {Province.class, City.class, County.class, Street.class},
        views = {StreetDetailInfo.class},
        version = 1,
        exportSchema = false)
public abstract class AddressDatabase extends RoomDatabase {
    public abstract AddressDao addressDao();
}
``` 

```java
@Dao
public interface AddressDao {
    // ...
    @Query("SELECT * FROM StreetDetailInfo WHERE id=:id")
    StreetDetailInfo loadStreetDetail(int id);
    // ...
}
``` 

## DAO

增删改查四种操作，对应四种注解`Insert`, `Delete`, `Update`, `Query`,
用得最多的还是`Query`。另外三种，定义的方法可以返回`void`，不关注影响的行；
`long` 只有一行受影响，`long[]`或`List<Long>` 多行受影响的返回值。
`long`表示是受影响行的`rowId`。

### Query 查询

可以在[AddressDao]()看到各种实现

#### 简单查询
```java
@Dao
public interface AddressDao {
    @Query("SELECT * FROM province")
    List<Province> loadAllProvinces();
}
```

#### 条件查询
```java
@Dao
public interface AddressDao {
    @Query("SELECT * FROM city where province_id=:id")
    List<City> loadCitiesByProvinceId(int id);
}
```

#### 只需要获取行的子集

这里没有使用到，可以参考官方文档
```java
public class NameTuple {
    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    @NonNull
    public String lastName;
}

@Dao
public interface MyDao {
    @Query("SELECT first_name, last_name FROM user")
    List<NameTuple> loadFullName();
}
``` 

#### 传入集合参数，通常是列表或数组

参考官方文档
```java
@Dao
public interface MyDao {
    @Query("SELECT first_name, last_name FROM user WHERE region IN (:regions)")
    List<NameTuple> loadUsersFromRegions(List<String> regions);
}
```
#### 响应式，`LiveData`和`RxJava2`

```java
@Dao
public interface MyDao {
    @Query("SELECT * FROM city")
    Flowable<List<City>> loadAllCities();

    @Query("SELECT * FROM city where province_id=:id")
    List<City> loadCitiesByProvinceId(int id);
}
```

#### Cursor, 官方不建议使用`Cursor`

#### 多表查询

参考官方文档，分别是查询的结果是一个`Entity`, 而另一个查询结果只是一个POJO对象。
```java
@Dao
public interface MyDao {
    @Query("SELECT * FROM book " +
           "INNER JOIN loan ON loan.book_id = book.id " +
           "INNER JOIN user ON user.id = loan.user_id " +
           "WHERE user.name LIKE :userName")
           List<Book> findBooksBorrowedByNameSync(String userName);
    
    @Query("SELECT user.name AS userName, pet.name AS petName " +
          "FROM user, pet " +
          "WHERE user.id = pet.user_id")
          LiveData<List<UserPet>> loadUserAndPetNames();

   // You can also define this class in a separate file, as long as you add the
   // "public" access modifier.
   static class UserPet {
       public String userName;
       public String petName;
   }
}
```

#### Transaction 事务处理

事务处理，认为其中所有的处理都是原子操作，如果其中某一步失败，则退回到事务操作之前，认为是全部失败。

所以事务处理实际上可以认为是多个操作的集合。具体可以参考官方文档，这里不讨论。

## Migration 升级（迁移）

 

 













