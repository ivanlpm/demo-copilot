import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  images: {
    remotePatterns: [
      {
        protocol: "https",
        hostname: "random-d.uk",
        pathname: "/**",
      },
    ],
  },
};

export default nextConfig;
