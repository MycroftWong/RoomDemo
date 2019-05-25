# Room 实践

彻底阅读了官方的`Room`文档，实现了部分功能

## 简介

`Room`提供SQLite的抽象层，实际上就是封装了SQLite, 同时使用注解，自动生成代码，这一点和`DBFlow`很像，估计也是借鉴了的。

### Room三大组件
* Database 持有数据库，保持和数据库的连接，使用`Room.databaseBuilder`或`Room.inMemoryDatabaseBuilder()`创建
* Entity 代表数据库中的表`table`
* DAO 包含操作数据库的方法

<!--![Room架构图](https://developer.android.com/images/training/data-storage/room_architecture.png)-->

### 使用

使用注解定义`Database`, `Entity`, `DAO`

`Database`定义为抽象类或接口

`Entity`中主键使用`PrimaryKey`标注...

`DAO`定义为接口，其中的所有方法需要使用`Query`, `Delete`, `Insert`, `Update`, `RawQuery`标注

具体参考
[Province.java](./app/src/main/java/com/mycroft/roomdeomo/entity/Province.java),
[City.java](./app/src/main/java/com/mycroft/roomdeomo/entity/City.java),
[County.java](./app/src/main/java/com/mycroft/roomdeomo/entity/County.java),
[Street.java](./app/src/main/java/com/mycroft/roomdeomo/entity/Street.java)

### Ignore的使用

在`Entity`中，使用`Ignore`标注的字段在存入数据库中时会被忽略，就像`Gson`中的`Export`

### AutoValue

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

