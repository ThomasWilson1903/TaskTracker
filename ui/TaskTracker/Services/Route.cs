using System;

namespace TaskTracker.Services;

public class Route
{
    public string Path { get; }

    public Type Type { get; }

    public Route(string path, Type type)
    {
        Path = path;
        Type = type;
    }
}
