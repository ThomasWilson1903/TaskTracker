using TaskTracker.Models;

namespace TaskTracker.Tests.Models;

public class UserTests
{
    [Fact]
    public void OnCreationSetGuid()
    {
        var user = new User();
        
        Assert.True(user.Id != Guid.Empty);
    }
}